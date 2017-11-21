// Copyright (c) 2014-2016 K Team. All Rights Reserved.
package org.kframework.frontend.kil;

import com.google.common.reflect.TypeToken;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.kframework.frontend.kil.loader.Constants;
import org.kframework.frontend.kil.visitors.Visitor;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * Please use {@link org.kframework.attributes.Att}
 * Represents either an explicit attribute on a {@link Bubble} or {@link Production},
 * or node metadata like location.
 * The inherited member attributes is used for location information
 * if this represents an explicitly written attribute.
 */
public class Attribute<T> extends ASTNode {

    public static final String TOKEN = "token";
    public static final String KLABEL = "klabel";
    public static final String RULE = "rule";
    public static final String CONFIG = "config";
    public static final String CONTEXT = "context";


    public static final Attribute<String> BRACKET = Attribute.of("bracket", "");
    public static final Attribute<String> NOT_IN_RULES = Attribute.of("notInRules", "");

    private Key<T> key;
    private T value;

    public static class Key<T> implements Serializable {
        private final TypeToken<T> typeToken;
        private final Annotation annotation;

        protected Key(TypeToken<T> typeToken, Annotation annotation) {
            this.typeToken = typeToken;
            this.annotation = annotation;
        }

        public TypeToken<T> getTypeToken() {
            return typeToken;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public static <T> Key<T> get(Class<T> cls, Annotation annotation) {
            return new Key<T>(TypeToken.of(cls), annotation);
        }

        public static <T> Key<T> get(Class<T> cls) {
            return new Key<T>(TypeToken.of(cls), null);
        }

        public static <T> Key<T> get(TypeToken<T> type) {
            return new Key<T>(type, null);
        }

        public static <T> Key<T> get(TypeToken<T> type, Annotation annotation) {
            return new Key<T>(type, annotation);
        }

        @Override
        public String toString() {
            if (getTypeToken().equals(TypeToken.of(String.class))) {
                return toString(getAnnotation());
            }
            String annotation = toString(getAnnotation());
            if (annotation != null) {
                return "@" + getTypeToken().toString() + "." + annotation;
            } else {
                return "@" + getTypeToken().toString();
            }
        }

        public static String toString(Annotation annotation) {
            if (annotation == null) return null;
            if (annotation instanceof Named) {
                return ((Named)annotation).value();
            }
            return annotation.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((annotation == null) ? 0 : annotation.hashCode());
            result = prime * result
                    + ((typeToken == null) ? 0 : typeToken.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Key<?> other = (Key<?>) obj;
            if (annotation == null) {
                if (other.annotation != null)
                    return false;
            } else if (!annotation.equals(other.annotation))
                return false;
            if (typeToken == null) {
                if (other.typeToken != null)
                    return false;
            } else if (!typeToken.equals(other.typeToken))
                return false;
            return true;
        }
    }

    public static Attribute<String> of(String key, String value) {
        return new Attribute<String>(keyOf(key), value);
    }

    public static Key<String> keyOf(String key) {
        return Key.get(String.class, Names.named(key));
    }

    public Attribute(Key<T> key, T value) {
        super();
        this.key = key;
        this.value = value;
    }

    public Attribute(Attribute<T> attribute) {
        super(attribute);
        key = attribute.key;
        value = attribute.value;
    }

    @Override
    public String toString() {
        return " " + this.getKey() + "(" + this.getValue() + ")";
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setKey(Key<T> key) {
        this.key = key;
    }

    public Key<T> getKey() {
        return key;
    }

    @Override
    public Attribute<T> shallowCopy() {
        return new Attribute<T>(this);
    }

    @Override
    protected <P, R, E extends Throwable> R accept(Visitor<P, R, E> visitor, P p) throws E {
        return visitor.complete(this, visitor.visit(this, p));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Attribute<?> other = (Attribute<?>) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
