package com.nttdata.bank.mapper;

import com.nttdata.bank.entity.YankiEntity;
import com.nttdata.bank.request.YankiRequest;
import com.nttdata.bank.response.YankiResponse;

/**
 * YankiMapper provides mapping methods to convert between YankiRequest,
 * YankiEntity, and YankiResponse objects. This class includes methods to map a
 * YankiRequest to a YankiEntity and to map a YankiEntity to a YankiResponse.
 */

public class YankiMapper {

	/**
	 * Maps a YankiRequest object to a YankiEntity object.
	 *
	 * @param yankiRequest The yanki request to map
	 * @return The mapped yanki entity
	 */
	public static YankiEntity mapperToEntity(YankiRequest yankiRequest) {
		YankiEntity yankiEntity = new YankiEntity();

		if (yankiRequest != null) {
			yankiEntity.setName(yankiRequest.getName());
			yankiEntity.setPhoneNumber(yankiRequest.getPhoneNumber());
			yankiEntity.setDocumentNumber(yankiRequest.getDocumentNumber());
		}

		return yankiEntity;
	}

	/**
	 * Maps a YankiEntity object to a YankiResponse object.
	 *
	 * @param yankiEntity The yanki entity to map
	 * @return The mapped yanki response
	 */
	public static YankiResponse mapperToResponse(YankiEntity yankiEntity) {
		YankiResponse yankiResponse = new YankiResponse();

		if (yankiEntity != null) {
			yankiResponse.setName(yankiEntity.getName());
			yankiResponse.setPhoneNumber(yankiEntity.getPhoneNumber());
		}

		return yankiResponse;
	}
}
