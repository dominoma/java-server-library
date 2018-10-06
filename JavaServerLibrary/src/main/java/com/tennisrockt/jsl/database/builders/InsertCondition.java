package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface InsertCondition<T> {
	boolean check(T obj) throws ServerException;
}
