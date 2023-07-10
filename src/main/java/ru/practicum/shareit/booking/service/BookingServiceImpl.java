package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public BookingDto save(Long bookerId, BookingRequestDto bookingRequestDto) throws NotFoundException, BadRequestException {
        checkStartAndEndDateBooking(bookingRequestDto);

        Long itemId = bookingRequestDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("вещи с id{" + itemId + "} нет в списке вещей"));

        List<Booking> bookingItemWithItemId = bookingRepository.findByItemAndStatusIn(item,
                List.of(BookingStatus.WAITING, BookingStatus.APPROVED), Sort.by(Sort.Direction.ASC, "start"));
        if (checkAvailableBookingDates(bookingRequestDto, bookingItemWithItemId)) {
            throw new BadRequestException("бронирование на указанные даты и время невозможно");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("вещь с id{" + itemId + "} не доступна для бронирования");
        }

        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new NotFoundException("бронирование владельцем своих же вещей не доступно");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + bookerId + "} нет в списке пользователей"));

        item.setAvailable(false);
        Booking booking = bookingRepository.save(bookingMapper.toBooking(bookingRequestDto, item, booker, BookingStatus.WAITING));

        return bookingMapper.toBookingDto(booking, itemMapper.toItemDto(item), userMapper.toUserDto(booker));
    }

    @Transactional
    @Override
    public BookingDto patchUpdate(Long bookingId, Long ownerId, Boolean approved)
            throws NotFoundException, BadRequestException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("бронирования с id{" + bookingId + "} нет в списке бронирований"));

        Long ownerIdItem = booking.getItem().getOwner().getId();
        if (!Objects.equals(ownerIdItem, ownerId)) {
            throw new NotFoundException("подтверждение бронирования может выполнить только владелец вещи");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("подтверждение бронирования уже выполнено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        booking.getItem().setAvailable(true);
        Booking bookingSave = bookingRepository.save(booking);

        return bookingMapper.toBookingDto(bookingSave, itemMapper.toItemDto(bookingSave.getItem()),
                userMapper.toUserDto(bookingSave.getBooker()));
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) throws NotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("бронирования с id{" + bookingId + "} нет в списке бронирований"));

        if (!Objects.equals(booking.getBooker().getId(), userId)) {
            if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
                throw new NotFoundException("операция может быть выполнена либо автором бронирования, либо владельцем вещи");
            }
        }

        return bookingMapper.toBookingDto(booking, itemMapper.toItemDto(booking.getItem()),
                userMapper.toUserDto(booking.getBooker()));
    }

    @Override
    public List<BookingDto> findAllBookingByUserId(Long userId, BookingState state, Integer from, Integer size)
            throws NotFoundException, BadRequestException {
        if (from < 0) {
            throw new BadRequestException("request param from{" + from + "} не может быть отрицательным");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        Sort sort = Sort.by(DESC, "start");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        switch (state) {
            case ALL:
                return bookingMapper.toBookingDto(bookingRepository.findByBooker(user, pageable));
            case CURRENT:
                return bookingMapper.toBookingDto(bookingRepository.findByBookerWhereStatusCurrent(user, pageable));
            case PAST:
                return bookingMapper.toBookingDto(bookingRepository.findByBookerAndEndBefore(user,
                        LocalDateTime.now(), pageable));
            case FUTURE:
                return bookingMapper.toBookingDto(bookingRepository.findByBookerAndStatusInAndStartAfter(user,
                        List.of(BookingStatus.APPROVED, BookingStatus.WAITING), LocalDateTime.now(), pageable));
            case WAITING:
                return bookingMapper.toBookingDto(bookingRepository.findByBookerAndStatus(user, BookingStatus.WAITING,
                        pageable));
            case REJECTED:
                return bookingMapper.toBookingDto(bookingRepository.findByBookerAndStatus(user, BookingStatus.REJECTED,
                        pageable));
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public List<BookingDto> findBookingForAllItemByUserId(Long userId, BookingState state, Integer from, Integer size)
            throws NotFoundException, BadRequestException {
        if (from < 0) {
            throw new BadRequestException("request param from{" + from + "} не может быть отрицательным");
        }

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя с id{" + userId + "} нет в списке пользователей"));

        Sort sort = Sort.by(DESC, "start");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, sort);
        switch (state) {
            case ALL:
                return bookingMapper.toBookingDto(bookingRepository.findBookingForAllItemByUser(owner, pageable));
            case CURRENT:
                return bookingMapper.toBookingDto(bookingRepository.findBookingAllItemByUserWhereStatusCurrent(owner,
                        pageable));
            case PAST:
                return bookingMapper.toBookingDto(bookingRepository.findBookingForAllItemByUserWhereEndBefore(owner,
                        LocalDateTime.now(), pageable));
            case FUTURE:
                return bookingMapper.toBookingDto(bookingRepository.findBookingForAllItemByUserWhereStartAfter(owner,
                        List.of(BookingStatus.APPROVED, BookingStatus.WAITING), LocalDateTime.now(), pageable));
            case WAITING:
                return bookingMapper.toBookingDto(bookingRepository.findBookingForAllItemByUser(owner,
                        BookingStatus.WAITING, pageable));
            case REJECTED:
                return bookingMapper.toBookingDto(bookingRepository.findBookingForAllItemByUser(owner,
                        BookingStatus.REJECTED, pageable));
            default:
                return Collections.emptyList();
        }
    }

    private void checkStartAndEndDateBooking(BookingRequestDto bookingRequestDto) throws BadRequestException {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new BadRequestException("дата окончания бронирования ранее даты начала бронирования");
        }

        if (bookingRequestDto.getEnd().isEqual(bookingRequestDto.getStart())) {
            throw new BadRequestException("дата окончания бронирования равна дате начала бронирования");
        }
    }

    private boolean checkAvailableBookingDates(BookingRequestDto bookingRequestDto, List<Booking> bookingItemWithItemId) {
        // (bookingStart <= booking.getEnd()) && (bookingEnd >= booking.getStart())
        LocalDateTime bookingStart = bookingRequestDto.getStart();
        LocalDateTime bookingEnd = bookingRequestDto.getEnd();
        for (Booking booking : bookingItemWithItemId) {
            if ((bookingStart.isBefore(booking.getEnd()) || bookingStart.isEqual(booking.getEnd()))
                    && ((bookingEnd.isAfter(booking.getStart())) || (bookingEnd.isEqual(booking.getStart())))) {
                return true; // найдено пересечение с датами уже имеющихся бронирований
            }
        }

        return false;
    }

}