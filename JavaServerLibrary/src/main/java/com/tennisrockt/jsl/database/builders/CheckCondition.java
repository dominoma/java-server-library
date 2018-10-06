package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface CheckCondition<T> {
	void check(T obj) throws ServerException;
}
