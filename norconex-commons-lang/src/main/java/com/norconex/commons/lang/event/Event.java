/* Copyright 2018 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.commons.lang.event;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A generic event implementation.
 * @author Pascal Essiembre
 * @param <T> the type of the event source
 * @since 2.0.0
 * @see IEventListener
 */
public class Event<T> {

    
    private final String name;
    private final T source;
    private final Throwable exception;

    /**
     * New event.
     * @param name event name
     * @param source object responsible for triggering the event
     */
    public Event(String name, T source) {
        this(name, source, null);
    }

    
    /**
     * New event.
     * @param name event name
     * @param source object responsible for triggering the event
     * @param exception exception
     */
    public Event(String name, T source, Throwable exception) {
        super();
        this.name = name;
        this.source = source;
        this.exception = exception;
    }
    
    /**
     * Gets the object representing the source of this event.
     * @return the subject
     */
    public Object getSource() {
        return source;
    }

    /**
     * Gets the event name.
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the exception, if any.
     * @return the exception or <code>null</code>
     */
    public Throwable getException() {
        return exception;
    }

    public boolean equalsName(Event<?> event) {
        if (event == null) {
            return false;
        }
        return equalsName(event.getName());
    }
    public boolean equalsName(String eventName) {
        return Objects.equals(name, eventName);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Event))
            return false;
        Event<?> castOther = (Event<?>) other;
        return new EqualsBuilder()
                .append(name, castOther.name)
                .append(source, castOther.source)
                .append(exception, castOther.exception)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(source)
                .append(exception)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("source", source)
                .append("exception", exception)
                .toString();
    }
}