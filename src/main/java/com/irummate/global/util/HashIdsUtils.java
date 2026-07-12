package com.irummate.global.util;

import org.hashids.Hashids;

public final class HashIdsUtils {

    private static final Hashids HASHIDS = new Hashids("irummate-user-id", 8);

    private HashIdsUtils() {
    }

    public static String encode(Long id) {
        if (id == null) {
            return null;
        }

        return HASHIDS.encode(id);
    }

    public static Long decode(String hashId) {
        if (hashId == null || hashId.isBlank()) {
            throw new IllegalArgumentException("Hashids value must not be blank.");
        }

        if (hashId.chars().allMatch(Character::isDigit)) {
            return Long.valueOf(hashId);
        }

        long[] decoded = HASHIDS.decode(hashId);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid Hashids value.");
        }

        return decoded[0];
    }
}
