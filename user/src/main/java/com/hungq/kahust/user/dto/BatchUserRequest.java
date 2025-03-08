package com.hungq.kahust.user.dto;

import java.util.List;

public record BatchUserRequest(List<Long> userIds) {}
