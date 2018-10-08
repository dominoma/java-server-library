package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.RequestException;

public interface CheckCondition<T> {
	void check(T obj) throws RequestException;
}
