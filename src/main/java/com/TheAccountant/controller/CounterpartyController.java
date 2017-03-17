package com.TheAccountant.controller;

import com.TheAccountant.controller.exception.ConflictException;
import com.TheAccountant.controller.exception.NotFoundException;
import com.TheAccountant.dao.CounterpartyDao;
import com.TheAccountant.model.counterparty.Counterparty;
import com.TheAccountant.model.user.AppUser;
import com.TheAccountant.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by tudor.grigoriu on 3/17/2017.
 */
@RestController
@RequestMapping(value = "/counterparties")
public class CounterpartyController {

    @Autowired
    private CounterpartyDao counterpartyDao;

    @Autowired
    private UserUtil userUtil;

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Counterparty> create(@RequestBody @Valid Counterparty counterparty) {
        try {
            Counterparty createdCounterparty = counterpartyDao.saveAndFlush(counterparty);
            return new ResponseEntity<>(createdCounterparty, HttpStatus.OK);
        } catch (DataIntegrityViolationException dive) {
            throw new ConflictException(dive.getMostSpecificCause().getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Counterparty>> findAll() {
        AppUser appUser = userUtil.extractLoggedAppUserFromDatabase();;
        if (appUser == null) {
            throw new NotFoundException("User not found");
        }
        return new ResponseEntity<>(counterpartyDao.fetchAll(appUser.getUsername()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody @Valid Counterparty counterparty) {

        Counterparty oldCounterparty = counterpartyDao.findOne(id);
        if (oldCounterparty == null) {
            throw new NotFoundException("Counterparty not found");
        }
        counterparty.setId(id);
        counterpartyDao.saveAndFlush(counterparty);
        return new ResponseEntity<>("Counterparty updated", HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {

        try {
            userUtil.extractLoggedAppUserFromDatabase();
            Counterparty counterparty = counterpartyDao.findOne(id);
            if(counterparty == null){
                throw new NotFoundException("Counterparty not found");
            }
            counterpartyDao.delete(id);
            counterpartyDao.flush();
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new NotFoundException(emptyResultDataAccessException.getMessage());
        }
        return new ResponseEntity<>("Counterparty deleted", HttpStatus.NO_CONTENT);
    }


}
