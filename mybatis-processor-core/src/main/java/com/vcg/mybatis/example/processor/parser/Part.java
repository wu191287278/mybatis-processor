package com.vcg.mybatis.example.processor.parser;

import java.util.*;

public class Part {

    private String name;

    private Type type;

    public Part(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public enum Type {

        BETWEEN(2, "IsBetween", "Between"), IS_NOT_NULL(0, "IsNotNull", "NotNull"), IS_NULL(0, "IsNull", "Null"), LESS_THAN(
                "IsLessThan", "LessThan"), LESS_THAN_EQUAL("IsLessThanEqual", "LessThanEqual"), GREATER_THAN("IsGreaterThan",
                "GreaterThan"), GREATER_THAN_EQUAL("IsGreaterThanEqual", "GreaterThanEqual"), BEFORE("IsBefore",
                "Before"), AFTER("IsAfter", "After"), NOT_LIKE("IsNotLike", "NotLike"), LIKE("IsLike",
                "Like"), STARTING_WITH("IsStartingWith", "StartingWith", "StartsWith"), ENDING_WITH("IsEndingWith",
                "EndingWith", "EndsWith"), IS_NOT_EMPTY(0, "IsNotEmpty", "NotEmpty"), IS_EMPTY(0, "IsEmpty",
                "Empty"), NOT_CONTAINING("IsNotContaining", "NotContaining", "NotContains"), CONTAINING(
                "IsContaining", "Containing", "Contains"), NOT_IN("IsNotIn", "NotIn"), IN("IsIn",
                "In"), NEAR("IsNear", "Near"), WITHIN("IsWithin", "Within"), REGEX("MatchesRegex",
                "Matches", "Regex"), EXISTS(0, "Exists"), TRUE(0, "IsTrue", "True"), FALSE(0,
                "IsFalse", "False"), NEGATING_SIMPLE_PROPERTY("IsNot",
                "Not"), SIMPLE_PROPERTY("Is", "Equals");

        // Need to list them again explicitly as the order is important
        // (esp. for IS_NULL, IS_NOT_NULL)
        private static final List<Type> ALL = Arrays.asList(IS_NOT_NULL, IS_NULL, BETWEEN, LESS_THAN, LESS_THAN_EQUAL,
                GREATER_THAN, GREATER_THAN_EQUAL, BEFORE, AFTER, NOT_LIKE, LIKE, STARTING_WITH, ENDING_WITH, IS_NOT_EMPTY,
                IS_EMPTY, NOT_CONTAINING, CONTAINING, NOT_IN, IN, NEAR, WITHIN, REGEX, EXISTS, TRUE, FALSE,
                NEGATING_SIMPLE_PROPERTY, SIMPLE_PROPERTY);

        public static final Collection<String> ALL_KEYWORDS;

        static {
            List<String> allKeywords = new ArrayList<>();
            for (Type type : ALL) {
                allKeywords.addAll(type.keywords);
            }
            ALL_KEYWORDS = Collections.unmodifiableList(allKeywords);
        }

        private final List<String> keywords;
        private final int numberOfArguments;

        /**
         * Creates a new {@link Type} using the given keyword, number of arguments to be bound and operator. Keyword and
         * operator can be {@literal null}.
         *
         * @param numberOfArguments
         * @param keywords
         */
        private Type(int numberOfArguments, String... keywords) {

            this.numberOfArguments = numberOfArguments;
            this.keywords = Arrays.asList(keywords);
        }

        private Type(String... keywords) {
            this(1, keywords);
        }

        /**
         * Returns the {@link Type} of the {@link Part} for the given raw propertyPath. This will try to detect e.g.
         * keywords contained in the raw propertyPath that trigger special query creation. Returns {@link #SIMPLE_PROPERTY}
         * by default.
         *
         * @param rawProperty
         * @return
         */
        public static Part.Type fromProperty(String rawProperty) {

            for (Part.Type type : ALL) {
                if (type.supports(rawProperty)) {
                    return type;
                }
            }

            return SIMPLE_PROPERTY;
        }

        /**
         * Returns all keywords supported by the current {@link Type}.
         *
         * @return
         */
        public Collection<String> getKeywords() {
            return Collections.unmodifiableList(keywords);
        }

        /**
         * Returns whether the the type supports the given raw property. Default implementation checks whether the property
         * ends with the registered keyword. Does not support the keyword if the property is a valid field as is.
         *
         * @param property
         * @return
         */
        protected boolean supports(String property) {

            for (String keyword : keywords) {
                if (property.endsWith(keyword)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Returns the number of arguments the propertyPath binds. By default this exactly one argument.
         *
         * @return
         */
        public int getNumberOfArguments() {
            return numberOfArguments;
        }

        /**
         * Callback method to extract the actual propertyPath to be bound from the given part. Strips the keyword from the
         * part's end if available.
         *
         * @param part
         * @return
         */
        public String extractProperty(String part) {

            String candidate = uncapitalize(part);

            for (String keyword : keywords) {
                if (candidate.endsWith(keyword)) {
                    return candidate.substring(0, candidate.length() - keyword.length());
                }
            }

            return candidate;
        }


        private static String uncapitalize(String str) {
            return changeFirstCharacterCase(str, false);
        }

        private static String changeFirstCharacterCase(String str, boolean capitalize) {
            if (str == null || str.trim().length() <= 0) {
                return str;
            }

            char baseChar = str.charAt(0);
            char updatedChar;
            if (capitalize) {
                updatedChar = Character.toUpperCase(baseChar);
            } else {
                updatedChar = Character.toLowerCase(baseChar);
            }
            if (baseChar == updatedChar) {
                return str;
            }

            char[] chars = str.toCharArray();
            chars[0] = updatedChar;
            return new String(chars, 0, chars.length);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return String.format("%s (%s): %s", name(), getNumberOfArguments(), getKeywords());
        }
    }

    @Override
    public String toString() {
        return "Part{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
