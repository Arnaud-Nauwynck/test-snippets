package fr.an.tests.reverseyarn.dto.app;

import java.io.Serializable;

import lombok.Value;

@Value
public final class LongRange /*extends Range*/ implements Serializable {
    
    private static final long serialVersionUID = 71849363892720L;

    private final long min;
    private final long max;
    
}
