package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.model.Song;
import com.epam.training.microservicefoundation.songservice.model.SongNotFoundException;
import com.epam.training.microservicefoundation.songservice.model.SongRecord;
import com.epam.training.microservicefoundation.songservice.model.SongRecordId;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import com.epam.training.microservicefoundation.songservice.service.Mapper;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import com.epam.training.microservicefoundation.songservice.service.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SongServiceImpl implements SongService {
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongRepository repository;
    private final Mapper<Song, SongRecord> songMapper;
    private final Validator<SongRecord> songRecordValidator;
    private final Validator<long[]> idParameterValidator;

    public SongServiceImpl(SongRepository repository, Mapper<Song, SongRecord> songMapper,
                           Validator<SongRecord> songRecordValidator,
                           Validator<long[]> idParameterValidator) {
        this.repository = repository;
        this.songMapper = songMapper;
        this.songRecordValidator = songRecordValidator;
        this.idParameterValidator = idParameterValidator;
    }


    @Transactional
    @Override
    public SongRecordId save(SongRecord songRecord) {
        log.info("Saving a song record '{}'", songRecord);
        if(!songRecordValidator.validate(songRecord)) {
            IllegalArgumentException illegalArgumentException =
                    new IllegalArgumentException("Saving invalid song record");

            log.error("Saving a song record with invalid value", illegalArgumentException);
            throw illegalArgumentException;
        }
        try {
            Song song = repository.persist(songMapper.mapToEntity(songRecord));
            return new SongRecordId(song.getId());
        } catch (DataIntegrityViolationException ex) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    String.format("Saving a song record with invalid parameters length or duplicate value '%s'",
                            ex.getLocalizedMessage()), ex);

            log.error("Saving a song record with invalid parameters length or duplicate value", illegalArgumentException);
            throw illegalArgumentException;
        }
    }

    @Transactional
    @Override
    public SongRecord update(SongRecord songRecord) {
        log.info("Updating a song record '{}'", songRecord);
        if(!songRecordValidator.validate(songRecord)) {
            IllegalArgumentException illegalArgumentException =
                    new IllegalArgumentException(String.format("Updating an invalid song record '%s'", songRecord));

            log.error("Updating a song record with invalid value", illegalArgumentException);
            throw illegalArgumentException;
        }

        try {
            Song song = repository.update(songMapper.mapToEntity(songRecord));
            return songMapper.mapToRecord(song);
        } catch (DataIntegrityViolationException ex) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    String.format("Updating a song record with invalid parameters length or duplicate value '%s'",
                            ex.getLocalizedMessage()), ex);

            log.error("Updating a song record with invalid parameters length or duplicate value",
                    illegalArgumentException);
            throw illegalArgumentException;
        }
    }

    @Transactional
    @Override
    public List<SongRecordId> deleteByIds(long[] ids) {
        log.info("Deleting Song(s) with id {}", ids);
        if(!idParameterValidator.validate(ids)) {
            IllegalArgumentException ex = new IllegalArgumentException("Id param was not validated, check your ids");
            log.error("Id param size '{}' should be less than 200 \nreason:", ids.length, ex);
            throw ex;
        }
        Arrays.stream(ids).forEach(repository::deleteById);
        log.debug("Songs with id(s) '{}' were deleted", ids);
        return Arrays.stream(ids).mapToObj(SongRecordId::new).collect(Collectors.toList());
    }

    @Override
    public SongRecord getById(long id) {
        log.info("Getting a song with id '{}'", id);
        Song song = repository.findById(id).orElseThrow(() -> new SongNotFoundException(String.format("Song was not " +
                "found with id '%d'", id)));

        return songMapper.mapToRecord(song);
    }

    @Transactional
    @Override
    public List<SongRecordId> deleteByResourceIds(long[] ids) {
        log.info("Deleting Song(s) with resource id(s) '{}'", ids);
        if(!idParameterValidator.validate(ids)) {
            IllegalArgumentException ex = new IllegalArgumentException("Id param was not validated, check your ids");
            log.error("Id param size '{}' should be less than 200 \nreason:", ids.length, ex);
            throw ex;
        }
        List<SongRecordId> deletedSongIds = new ArrayList<>();
        for(long resourceId: ids) {
            Optional<Song> byResourceId = repository.findByResourceId(resourceId);
            byResourceId.ifPresent(song -> {
                repository.delete(song);
                deletedSongIds.add(new SongRecordId(song.getId()));
            });
        }
        log.debug("Songs with resource id(s) '{}' were deleted", deletedSongIds);
        return deletedSongIds;
    }
}
