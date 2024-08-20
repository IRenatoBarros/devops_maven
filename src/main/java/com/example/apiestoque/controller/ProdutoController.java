package com.example.apiestoque.controller;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final Validator validator;


    @Autowired
    public ProdutoController(ProdutoRepository produtoRepository, Validator validator){
        this.produtoRepository = produtoRepository;
        this.validator = validator;
    }

    @GetMapping("/selecionar")
    public List<Produto> listarProdutos(){
        return produtoRepository.findAll();
    }

    @PostMapping("/inserir")
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto, BindingResult result){

        if(result.hasErrors()){
            StringBuilder errorMessage = new StringBuilder("Erros de validação:");
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(" ").append(error.getDefaultMessage()).append(";");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }else{
            Produto produto2 = produtoRepository.save(produto);
            if (produto2.getId() == produto.getId()) {
                return ResponseEntity.ok("Inserido com sucesso");
            } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("IDs diferentes");
        }}


    }


    @DeleteMapping("/excluir/{id}")
    @Operation()
    public ResponseEntity<String> excluirProduto(@PathVariable Long id){

        if (produtoRepository.findById(id).map(produto -> {
            produtoRepository.deleteById(id);
            return true;}).orElse(false)) {
            return ResponseEntity.ok("Produto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("vc pediu para excluir algo que não existe, ou seja DEU CERTO! nada - nada = nada. Objetivo alcançado, parabéns garotão!");
        }
        }



    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarProduto(@PathVariable Long id,@RequestBody Map<String,Object> produtoAtualizado, BindingResult result){

        if(result.hasErrors()){
            StringBuilder errorMessage = new StringBuilder("Erros de validação:");
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(" ").append(error.getDefaultMessage()).append(";");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }else{
        Optional<Produto> produtoExistente = produtoRepository.findById(id);


        if(produtoExistente.isPresent()){
            Produto produto = produtoExistente.get();

            if(produtoAtualizado.containsKey("nome")){
                produto.setNome((String) produtoAtualizado.get("nome"));
            }
            if(produtoAtualizado.containsKey("descricao")){
                produto.setDescricao((String) produtoAtualizado.get("descricao"));
            }
            if(produtoAtualizado.containsKey("preco")){
                produto.setPreco((Double) produtoAtualizado.get("preco"));
            }
            if(produtoAtualizado.containsKey("quantidadeEstoque")){
                produto.setQuantidadeEstoque((Integer) produtoAtualizado.get("quantidadeEstoque"));
            }

            Produto produto2 = produtoRepository.save(produto);
            if (produto2.getId() == produto.getId()) {
                return ResponseEntity.ok("Campo atualizado com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("IDs diferentes");
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto com ID "+id+" não encontrado");
        }
    }}



    public Map<String, String> validarProduto(BindingResult resultado){
        Map<String, String> erros = new HashMap<>();
        for(FieldError error : resultado.getFieldErrors()){
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }

}
