package com.example.jwt_redis.util;

import java.util.Optional;

public class ClassUtils {
    // 이러한 Optional 객체를 사용하면 예상치 못한 NullPointerException 예외를 제공되는 메소드로 간단히 회피할 수 있다.
    // 즉, 복잡한 조건문 없이도 널(null) 값으로 인해 발생하는 예외를 처리할 수 있게 된다.
    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clazz) {
        // clazz가 null 이 아니고 해당 인스턴스라면 캐스팅을 해주고 그게 아니면 empty를 반환한다.
        return clazz != null && clazz.isInstance(o) ? Optional.of(clazz.cast(o)) : Optional.empty();

        // of() 메소드나 ofNullable() 메소드를 사용하여 Optional 객체를 생성할 수 있다.
        // of() 메소드는 null이 아닌 명시된 값을 가지는 Optional 객체를 반환한다.
        // 만약 of() 메소드를 통해 생성된 Optional 객체에 null이 저장되면 NullPointerException 예외가 발생한다.
    }
}
