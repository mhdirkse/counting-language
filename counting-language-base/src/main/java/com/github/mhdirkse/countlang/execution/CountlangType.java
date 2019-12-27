package com.github.mhdirkse.countlang.execution;

public enum CountlangType {
    UNKNOWN(Object.class),
    INT(Integer.class),
    BOOL(Boolean.class);
    
    private final Class<?> implementationClass;
    
    CountlangType(Class<?> implementationClass) {
        this.implementationClass = implementationClass;
    }

    public boolean matches(Class<?> clazz) {
        return implementationClass.isAssignableFrom(clazz); 
    }

    public static CountlangType typeOf(Object value) {
        for(CountlangType t: CountlangType.values()) {
            if(t == CountlangType.UNKNOWN) {
                continue;
            }
            if(t.implementationClass.isAssignableFrom(value.getClass())) {
                return t;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Value has a type that is not supported by Countlang: %s",
                value.toString()));
    }
}
