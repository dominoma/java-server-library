package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.RequestException;

public interface InsertCondition<T> {
	boolean check(T obj) throws RequestException;
}
