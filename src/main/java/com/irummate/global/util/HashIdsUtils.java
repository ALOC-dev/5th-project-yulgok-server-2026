package com.irummate.global.util;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashIdsUtils {

    private final Hashids hashids;

    public HashIdsUtils(@Value("${hashids.salt}") String salt,
                         @Value("${hashids.minlength}") String length) {
        this.hashids = new Hashids(salt, Integer.parseInt(length));
    }

    public String encode(Long id) {
        if (id == null) {
            return null;
        }

        return hashids.encode(id);
    }

    public Long decode(String hashId) {
        if (hashId == null || hashId.isBlank()) {
            throw new IllegalArgumentException("Hashids value must not be blank.");
        }

        if (hashId.chars().allMatch(Character::isDigit)) {
            return Long.valueOf(hashId);
        }

        long[] decoded = hashids.decode(hashId);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid Hashids value.");
        }

        return decoded[0];
    }
}
