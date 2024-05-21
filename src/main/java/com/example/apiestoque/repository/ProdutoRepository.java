package com.example.apiestoque.repository;

import com.example.apiestoque.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Modifying
    @Query("DELETE FROM Produto e where e.id = ?1")
    void deleteById(Long id);

    @Modifying
    @Query("SELECT p FROM Produto p where p.nome like %?1% and p.preco <= ?2")
    List<Produto> findByNomeLikeIgnoreCaseAndPrecoLessThan(String nome,Double preco_lessThen);

}
