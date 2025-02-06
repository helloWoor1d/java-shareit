package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestGetDto;
import ru.practicum.shareit.request.dto.ItemRequestMapping;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Header.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;
    private final ItemRequestMapping itemRequestMapping;
    private final ItemController itemController;
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestGetDto> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                           @Valid @RequestBody ItemRequestCreateDto requestDto) {
        ItemRequest request = requestService.createRequest(itemRequestMapping.fromDto(requestDto, userId), userId);
        return ResponseEntity.ok(itemRequestMapping.toDto(request));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestGetDto>> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        List<ItemRequest> userRequests = requestService.getUserRequests(userId);
        Map<Long, List<ItemForRequestDto>> responses = itemController
                .getItemsByRequestId(userRequests.stream()
                        .map(ItemRequest::getId)
                        .collect(Collectors.toList()));

        List<ItemRequestGetDto> result = userRequests.stream()
                .map(r ->  itemRequestMapping.toDto(r, responses.get(r.getId())))
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestGetDto>> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<ItemRequest> requests = requestService.getAllRequests(userId, from, size);
        return ResponseEntity.ok(requests.stream()
                .map(itemRequestMapping::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestGetDto> getRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @PathVariable Long requestId) {
        ItemRequest request = itemRequestService.getRequest(requestId, userId);
        Map<Long, List<ItemForRequestDto>> responses = itemController.getItemsByRequestId(List.of(requestId));
        return ResponseEntity.ok(itemRequestMapping.toDto(request, responses.get(requestId)));
    }
}
