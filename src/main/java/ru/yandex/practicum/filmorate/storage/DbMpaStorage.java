package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DbMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public MpaRating getMpa(Long id) {
        final String sqlQuery = "select * FROM ratings " +
                "where rating_id = ?";
        final List<MpaRating> mpas = jdbcTemplate.query(sqlQuery, this::makeMpa, id);
        if (mpas.size() != 1) {
            return null;
        }
        return mpas.get(0);
    }

    @Override
    public List<MpaRating> getAllMpa() {
        final String sqlQuery = "select * FROM ratings";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    private MpaRating makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(rs.getInt("rating_id"),
                rs.getString("rating_name")
        );
    }

}
