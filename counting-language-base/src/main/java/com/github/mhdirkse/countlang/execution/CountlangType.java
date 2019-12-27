package com.github.mhdirkse.countlang.execution;

public enum CountlangType {
    UNKNOWN(Object.class, null),
    INT(Integer.class, new Integer(1)),
    BOOL(Boolean.class, new Boolean(true));
    
    private final Class<?> implementationClass;
    private final Object example;

    CountlangType(Class<?> implementationClass, Object example) {
        this.implementationClass = implementationClass;
        this.example = example;
    }

    public boolean matches(Class<?> clazz) {
        return implementationClass.isAssignableFrom(clazz); 
    }

    public Object getExample() {
        return example;
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
