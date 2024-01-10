package com.example.Assignment.repository;

import com.example.Assignment.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note,String> {

}
