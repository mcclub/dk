package com.common.bean.page;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Order implements Serializable {
    private static final long serialVersionUID = -3078342809727773232L;
    private static final Order.Direction DEFAULT_DIRECTION;
    private String property;
    private Order.Direction direction;

    public Order() {
        this.direction = DEFAULT_DIRECTION;
    }

    public Order(String property, Order.Direction direction) {
        this.direction = DEFAULT_DIRECTION;
        this.property = property;
        this.direction = direction;
    }

    public static Order asc(String property) {
        return new Order(property, Order.Direction.asc);
    }

    public static Order desc(String property) {
        return new Order(property, Order.Direction.desc);
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Order.Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Order.Direction direction) {
        this.direction = direction;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            Order other = (Order)obj;
            return (new EqualsBuilder()).append(this.getProperty(), other.getProperty()).append(this.getDirection(), other.getDirection()).isEquals();
        }
    }

    public int hashCode() {
        return (new HashCodeBuilder(17, 37)).append(this.getProperty()).append(this.getDirection()).toHashCode();
    }

    static {
        DEFAULT_DIRECTION = Order.Direction.desc;
    }

    public static enum Direction {
        asc,
        desc;

        private Direction() {
        }

        public static Order.Direction fromString(String value) {
            return valueOf(value.toLowerCase());
        }
    }
}