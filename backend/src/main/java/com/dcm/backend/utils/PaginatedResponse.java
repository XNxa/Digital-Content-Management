package com.dcm.backend.utils;

import java.util.Collection;

public record PaginatedResponse<T>(Collection<T> collection, long size) {
}
