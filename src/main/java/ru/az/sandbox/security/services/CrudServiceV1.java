package ru.az.sandbox.security.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.exceptions.ZException;

public interface CrudServiceV1<TReq, TRes, TPageReq> {

	TRes create(TReq dto) throws ZException;
	TRes update(TReq dto) throws ZException;
	TRes deleteById(Long id) throws ZException;
	TRes findById(Long id) throws ZException;
	void changeEntityStatus(Long id, String status) throws ZException;
	List<TRes> findAll(TPageReq request);
	Page<TRes> findAllAsPage(TPageReq request);
	long count();
	long count(EntityStatus status);
	
}
