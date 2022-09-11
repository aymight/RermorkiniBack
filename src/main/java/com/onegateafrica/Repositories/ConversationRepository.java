package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Entities.Remorqueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {



	@Query(value = "SELECT * FROM Conversation WHERE (consommateur1_id = :consommateur1Id AND consommateur2_id = :consommateur2Id) " +
			"OR (consommateur2_id = :consommateur1Id AND consommateur1_id = :consommateur2Id)" , nativeQuery = true)

	Optional<Conversation> findConversation(@Param(value="consommateur1Id") Long consommateur1Id, @Param(value="consommateur2Id") Long consommateur2Id);
	@Query(value = "SELECT * FROM Conversation WHERE consommateur1_id = :consommateurId OR consommateur2_id = :consommateurId", nativeQuery = true)
	Optional<List<Conversation>> findAllConversations(@Param(value="consommateurId") Long consommateurId);
}

