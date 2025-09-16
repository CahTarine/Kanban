package com.projeto.quadrokanban.adapter.output.repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;
import com.projeto.quadrokanban.adapter.output.mapper.TaskMapper;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;


@Repository
public class TaskRepository implements TaskOutputPort{
	
	private final JdbcTemplate jdbcTemplate;
	private final TaskMapper taskMapper;
	private final BeanPropertyRowMapper<TaskEntity> rowMapper = new BeanPropertyRowMapper<>(TaskEntity.class);
	
	public TaskRepository (JdbcTemplate jdbcTemplate, TaskMapper taskMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.taskMapper = taskMapper;
	}
	
	@Override
	public List<Task> findAll() {
		String sql = "SELECT * FROM get_all_tasks()";
		return jdbcTemplate.query(sql, rowMapper)
				.stream()
				.map(taskMapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<Task> findById(Long id) {
		String sql = "SELECT * FROM get_task_with_id(?)";
		// Encontra a entidade e, se existir, a converte para o domínio
		return jdbcTemplate.query(sql, rowMapper, id)
				.stream()
				.findFirst()
				.map(taskMapper::toDomain);
	}

	@Override
	public List<Task> findAllByTitleContainingIgnoreCase(String title) {
		String sql = "SELECT * FROM get_task_by_title(?)";
		
		return jdbcTemplate.query(sql, rowMapper, "%" + title + "%")
				.stream()
				.map(taskMapper::toDomain)
				.collect(Collectors.toList());
	}
	

	@Override
	public Task save(Task task) {
	    TaskEntity taskEntity = taskMapper.toEntity(task);
	    // Call é usado para chamar Procedures e SELECT para Functions;
	    String sql = "{? = call upsert_task(?, ?, ?, ?, ?)}";

	    if (taskEntity.getId() == null) {
//	    	INSERT
//	        
//	        Chamando a procedure para inserir um novo registro
//	    	Statement é interface JDBC para comunicação com BD, usada para executar funções e procedures.
	        jdbcTemplate.execute(sql, (CallableStatement cs) -> {
//	            Prepara uma comunicação entre a aplicação e o banco de dados para que ele possa enviar de volta
//	            o id (p_id) que foi definido como INOUT.
	            cs.registerOutParameter(1, Types.BIGINT);
	            cs.setNull(2, Types.BIGINT);  // Define o primeiro parâmetro (p_id) como null para o insert
//	           Requisição POST manda os dados para controller, que manda para UseCase, que faz a chamada do save,
//	           onde a model é convertida para entity e inserimos no cs os valores do JSON que vieram do controller(insomnia, por exemplo).
	            cs.setString(3, taskEntity.getTitle());
	            cs.setString(4, taskEntity.getDescription());
	            cs.setString(5, taskEntity.getStatus().name());
	            cs.setLong(6, taskEntity.getBoardId());
	            
	            cs.execute(); // Gatilho que inicia o trabalho da procedure.
	            
//	           Momento em que a API le o id que foi retornado e salva na entity para manter o objeto atualizado na memória
	            taskEntity.setId(cs.getLong(1));
	            return null; // Retorno do callback
	        });

	    } else {
	        // UPDATE
	        
	        // Chamando a procedure para atualizar um registro existente
	        jdbcTemplate.execute(sql, (CallableStatement cs) -> {
	        	cs.registerOutParameter(1, Types.BIGINT);
	            cs.setLong(2, taskEntity.getId());
	            cs.setString(3, taskEntity.getTitle());
	            cs.setString(4, taskEntity.getDescription());
	            cs.setString(5, taskEntity.getStatus().name());
	            cs.setLong(6, taskEntity.getBoardId());
	            
	            cs.execute();
	            return null; // Retorno do callback
	        });
	    }
	    
//	    Transforma a entity em Task para que o UseCase trabalhe apenas com o modelo de dominio e não tenha 
//	    nenhuma relação com o banco e dados.
	    return taskMapper.toDomain(taskEntity);
	}
	
	
	@Override
	public void deleteById(Long id) {
		String sql = "call pr_delete_task(?)";
		jdbcTemplate.execute(sql, (CallableStatement cs) -> {
			cs.setLong(1, id);
			
			cs.execute();
			
			return null;
		});
		
	}


}
