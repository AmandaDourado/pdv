package com.example.pdv.controller;

import com.example.pdv.dto.ResponseDTO;
import com.example.pdv.entity.User;
import com.example.pdv.exceptions.NoItemException;
import com.example.pdv.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(@Autowired UserService userService){
        this.userService =  userService;
    }

    @GetMapping()
    public ResponseEntity getAll(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity post(@RequestBody User user){
        try{
            user.setEnabled(true);
            return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
        } catch(Exception error){
            return new ResponseEntity<>(error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity put(@RequestBody User user){
        try{
            return new ResponseEntity<>(userService.update(user),HttpStatus.OK);
        } catch (NoItemException error){
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()),HttpStatus.BAD_REQUEST);
        } catch(Exception error){
            return new ResponseEntity<>(new ResponseDTO(error.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id){
        try {
            userService.deleteById(id);
            return new ResponseEntity(new ResponseDTO("Usuário removido com sucesso"), HttpStatus.OK);
        } catch(EmptyResultDataAccessException error){
            return new ResponseEntity<>(new ResponseDTO("Não foi possivel localizar o usuário"), HttpStatus.INTERNAL_SERVER_ERROR);
         }catch(Exception error){
            return new ResponseEntity<>(error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
