package com.hungq.kahust.room.dto;

import java.util.List;

public record UserBatchRequest(List<Long> userIds) {}
