package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.projeto.quadrokanban.adapter.output.entity.UserEntity;
import com.projeto.quadrokanban.adapter.output.mapper.UserMapper;
import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.core.port.output.UserOutputPort;

@Repository
public class UserRepository implements UserOutputPort {
	
	private final JdbcTemplate jdbcTemplate;
	private final UserMapper userMapper;
	private final BeanPropertyRowMapper<UserEntity> rowMapper = new BeanPropertyRowMapper<>(UserEntity.class);
	
	
	public UserRepository(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.userMapper = userMapper;
	}
	
	@Override
	public List<User> findAllUsers(){
		String sql = "SELECT * FROM users.get_all_users()";
		return jdbcTemplate.query(sql, rowMapper).stream().map(userMapper::toDomain).collect(Collectors.toList());
	}
	
	@Override
	public Optional<User> findUserById(Long id){
		String sql = "SELECT * FROM users.get_user_by_id(?)";
		return jdbcTemplate.query(sql, rowMapper, id)
				.stream().findFirst().map(userMapper::toDomain);
	}
	
	@Override
	public List<User> findUserByName(String name){
		String sql = "SELECT * FROM users.get_user_by_name(?)";
		return jdbcTemplate.query(sql, rowMapper, "%" + name + "%").stream()
				.map(userMapper::toDomain).collect(Collectors.toList());
	}
	
	@Override
	public User savedUser(User user) {
		UserEntity userEntity = userMapper.toEntity(user);
		
		String sql = "{ ? = call users.upsert_user(?, ?, ?) }";
		
		if (userEntity.getId() == null) {
			jdbcTemplate.execute(sql, (CallableStatement cs) -> {
			cs.registerOutParameter(1, Types.BIGINT);
			cs.setNull(2, Types.BIGINT);
			cs.setString(3, userEntity.getName());
			cs.setString(4, userEntity.getEmail());
			
			cs.execute();
			userEntity.setId(cs.getLong(1));
			return null;
		});
	} else {
		jdbcTemplate.execute(sql, (CallableStatement cs) -> {
			cs.registerOutParameter(1, Types.BIGINT);
			cs.setLong(2, userEntity.getId());
			cs.setString(3, userEntity.getName());
			cs.setString(4, userEntity.getEmail());
			
			cs.execute();
			return null;
			});
		}
		return userMapper.toDomain(userEntity);
	}
	
	@Override
	public void deleteUserById(Long id) {
		String sql = "call pr_delete_user(?)";
		jdbcTemplate.execute(sql, (CallableStatement cs) -> {
			cs.setLong(1, id);
			cs.execute();
			
			return null;
		});
	}
	
	
	

}
