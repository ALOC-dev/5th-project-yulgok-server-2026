package com.dormmatch.domain.matching.util;


import com.pgvector.PGvector;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MatchingVectorUtils {
    public static PGvector toPGvector(float[] vector){
        return new PGvector(vector);
    }
}
