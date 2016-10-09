package com.myMoneyTracker.converter;

import com.myMoneyTracker.dto.user.AppUserDTO;
import com.myMoneyTracker.model.user.AppUser;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

/**
 * Dozer converter class between AppUser and AppUserDTO
 * @author Floryn
 */
public class AppUserConverter {

    public AppUserDTO convertTo(AppUser appUser) {

        AppUserDTO destObject = new AppUserDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(appUser, destObject);
        return destObject;
    }

    public AppUser convertFrom(AppUserDTO appUserDTO) {

        AppUser destObject = new AppUser();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        mapper.map(appUserDTO, destObject);
        return destObject;
    }
}
