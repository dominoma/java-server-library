package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.RequestException;

public interface InsertCallback<T> {
	Object parse(T value) throws RequestException;
}
