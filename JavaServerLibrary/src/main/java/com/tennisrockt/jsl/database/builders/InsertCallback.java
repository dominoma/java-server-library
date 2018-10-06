package com.tennisrockt.jsl.database.builders;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface InsertCallback<T> {
	Object parse(T value) throws ServerException;
}
