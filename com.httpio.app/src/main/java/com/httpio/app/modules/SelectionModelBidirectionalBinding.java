package com.httpio.app.modules;

// Take from https://gist.github.com/eckig/03c6c2d6920c69b79ad4

import javafx.beans.WeakListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionModel;

import java.lang.ref.WeakReference;

public class SelectionModelBidirectionalBinding<T> implements ChangeListener<T>, WeakListener {

	private final WeakReference<Property<T>> propertyRef1;
	private final WeakReference<SelectionModel<T>> propertyRef2;

	private boolean updating = false;
	private final int cachedHashCode;

	public SelectionModelBidirectionalBinding(Property<T> property1, SelectionModel<T> property2) {
		cachedHashCode = property1.hashCode() * property2.hashCode();

		propertyRef1 = new WeakReference<>(property1);
		propertyRef2 = new WeakReference<>(property2);
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	@Override
	public boolean wasGarbageCollected() {
		return (getProperty1() == null) || (getProperty2() == null);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		final Object propertyA1 = getProperty1();
		final Object propertyA2 = getProperty2();

		if ((propertyA1 == null) || (propertyA2 == null)) {
			return false;
		}

		if (obj instanceof SelectionModelBidirectionalBinding) {

			final SelectionModelBidirectionalBinding<?> otherBinding = (SelectionModelBidirectionalBinding<?>) obj;

			final Object propertyB1 = otherBinding.getProperty1();
			final Object propertyB2 = otherBinding.getProperty2();

			if ((propertyB1 == null) || (propertyB2 == null)) {
				return false;
			}

			if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
				return true;
			}

            return propertyA1 == propertyB2 && propertyA2 == propertyB1;
		}

		return false;
	}

	@Override
	public void changed(ObservableValue<? extends T> sourceProperty, T oldValue, T newValue) {
		if (!updating) {
			final Property<T> property1 = propertyRef1.get();
			final SelectionModel<T> property2 = propertyRef2.get();

			if (property1 == null || property2 == null) {
				if (property1 != null) {
					property1.removeListener(this);
				}

				if (property2 != null) {
					property2.selectedItemProperty().removeListener(this);
				}

			} else {
				try {
					updating = true;

					if (property1 == sourceProperty) {
						property2.select(newValue);
					} else {
						property1.setValue(newValue);
					}

				} finally {
					updating = false;
				}
			}
		}
	}

	private Object getProperty1() {
		return propertyRef1.get();
	}

	private Object getProperty2() {
		return propertyRef2.get();
	}
}
