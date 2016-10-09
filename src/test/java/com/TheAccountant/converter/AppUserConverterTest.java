package com.TheAccountant.converter;

import static org.junit.Assert.assertEquals;

import java.util.Currency;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.TheAccountant.dto.user.AppUserDTO;
import com.TheAccountant.model.user.AppUser;

/**
 * Test class for dozer converter between AppUser and AppUserDTO
 * @author Tudor
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-config.xml" })
public class AppUserConverterTest {

    @Autowired
    AppUserConverter appUserConverter;

    @Test
    public void shouldMapAppUserToAppUserDTO() {

        AppUser appUser = createAppUser("Floryn");
        AppUserDTO appUserDTO = appUserConverter.convertTo(appUser);
        assertEquals(appUser.getUserId(), appUserDTO.getUserId());
        assertEquals(appUser.getSurname(), appUserDTO.getSurname());
        assertEquals(appUser.getFirstName(), appUserDTO.getFirstName());
        assertEquals(appUser.getUsername(), appUserDTO.getUsername());
        assertEquals(appUser.getDefaultCurrency().getCurrencyCode(), appUserDTO.getDefaultCurrency());
    }

    @Test
    public void shouldMapAppUserDTOToAppUser() {

        AppUserDTO appUserDTO = createAppUserDTO();
        AppUser appUser = appUserConverter.convertFrom(appUserDTO);
        assertEquals(appUserDTO.getFirstName(), appUser.getFirstName());
        assertEquals(appUserDTO.getUsername(), appUser.getUsername());
        assertEquals(appUserDTO.getSurname(), appUser.getSurname());
        assertEquals(appUserDTO.getUserId(), appUser.getUserId());
    }

    private AppUserDTO createAppUserDTO() {

        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setSurname("SURNAME");
        appUserDTO.setUsername("USERNAME");
        appUserDTO.setFirstName("FIRST_NAME");
        appUserDTO.setUserId(2l);
        return appUserDTO;
    }

    private AppUser createAppUser(String firstName) {

        AppUser appUser = new AppUser();
        appUser.setFirstName(firstName);
        appUser.setSurname("yacob");
        appUser.setPassword("TEST_PASS");
        appUser.setUsername("florynyacob");
        appUser.setBirthdate(new Date());
        appUser.setEmail("my-money-tracker@gmail.com");
        appUser.setDefaultCurrency(Currency.getInstance("RON"));
        return appUser;
    }
}
