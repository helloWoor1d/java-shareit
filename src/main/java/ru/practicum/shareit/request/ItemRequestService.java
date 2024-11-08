package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService {
    private final EntityManager entityManager;
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    public ItemRequest createRequest(ItemRequest request, long userId) {
        userService.getUser(userId);

        request.setCreated(LocalDateTime.now());
        ItemRequest createdRequest = requestRepository.save(request);
        log.debug("Создан запрос с id {}", createdRequest.getId());
        return createdRequest;
    }

    public List<ItemRequest> getUserRequests(long userId) {
        userService.getUser(userId);
        log.debug("Получен список запросов пользователя с id {}", userId);
        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    public List<ItemRequest> getAllRequests(Long userId, int from, int size) {
        userService.getUser(userId);

        Pageable pageRequest = PageRequest.of(from, size, Sort.by("create").descending());
        log.debug("Получен список запросов, созданных другими пользователями");
        return requestRepository.findAllByRequesterIdIsNot(userId, pageRequest);
    }

    public ItemRequest getRequest(Long requestId, Long userId) {
        userService.getUser(userId);

        log.debug("Получен запрос с id {}", requestId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не был найден"));
    }

    public ItemRequest getItemRequestReference(Long requestId) {
        if (requestId != null) {
            return entityManager.getReference(ItemRequest.class, requestId);
        } else {
            return null;
        }
    }
}
