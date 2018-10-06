package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.RequestException;

public interface BuilderCallback<T> {
	void build(T builder) throws RequestException;
}
