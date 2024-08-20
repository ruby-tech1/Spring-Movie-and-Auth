package com.ruby_dev.movieapi.dto;

import java.util.List;

public record UserPageResponse(List<UserDto> users,
                               Integer pageNumber,
                               Integer pageSize,
                               int totalElements,
                               int totalPages,
                               boolean isLast) {
}
