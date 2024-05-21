package com.example.apiestoque.service;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import java.lang.reflect.Field;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProdutoService{

    private final ProdutoRepository produtoRepository;
    private final Validator validator;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository, Validator validator) {
        this.produtoRepository = produtoRepository;
        this.validator = validator;
    }
    public List<Produto> buscarTodosProdutos() {
        return produtoRepository.findAll();
    }
    public Optional<Produto> buscarProdutoPorID(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public boolean existeProduto(Produto produto) {
        return produtoRepository.existsById(Long.valueOf(produto.getId()));
    }

    public boolean existeProduto(Long id) {
        return produtoRepository.existsById(id);
    }

    public void excluirProduto(Long id) {
        produtoRepository.deleteById(id);
    }

    public void excluirProduto(Produto produto) {
        produtoRepository.deleteById(Long.valueOf(produto.getId()));
    }

    public boolean atualizarProduto(Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = produtoRepository.findById(Long.valueOf(produtoAtualizado.getId()));
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoRepository.save(produto);
            return true;
        } else {
            return false;
        }
    }

    public String atualizarProdutoParcial(Long id, Map<String, Object> updates) {
        Optional<Produto> produtoExistente = produtoRepository.findById(id);
        StringBuilder errorMessage = new StringBuilder("Erros de validação:");
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();

            if (updates.containsKey("nome")) {
                produto.setNome((String) updates.get("nome"));
            }
            if (updates.containsKey("descricao")) {
                produto.setDescricao((String) updates.get("descricao"));
            }
            if (updates.containsKey("preco")) {
                produto.setPreco((Double) updates.get("preco"));
            }
            if (updates.containsKey("quantidadeEstoque")) {
                produto.setQuantidadeEstoque((Integer) updates.get("quantidadeEstoque"));
            }

            BeanPropertyBindingResult erros = new BeanPropertyBindingResult(produto, "produto");

            updates.forEach((propertyName, propertyValue) -> {
                validarEAtualizarPropriedade(produto, propertyName, propertyValue, erros);
            });

            if (erros.hasErrors()) {
                for (FieldError error : erros.getFieldErrors()) {
                    errorMessage.append(" ").append(error.getDefaultMessage()).append(";");
                }
                return errorMessage.toString();
            }

            produtoRepository.save(produto);
            return errorMessage.toString();
        } else {
            return errorMessage.toString();
        }
    }

    private void validarEAtualizarPropriedade(Produto produto, String propertyName, Object propertyValue, BeanPropertyBindingResult errors) {

        //todo: DataBinder é uma ponte entre o modelo de dados e a representação visual ou a lógica de negócio da aplicação.
        DataBinder dataBinder = new DataBinder(produto, "produto"); // Observe que aqui também definimos o nome do objeto
        dataBinder.setValidator(validator);

        // Define o novo valor
        // Usa a classe MutablePropertyValues, que é uma implementação de PropertyValues
        // usada para armazenar e manipular propriedades de objetos.
        // Atualiza o valor da propriedade
        dataBinder.bind(new MutablePropertyValues(Collections.singletonMap(propertyName, propertyValue)));

        // Realiza a validação
        dataBinder.validate();

        // Se houver erros de validação, os adicionamos ao objeto errors original
        if (dataBinder.getBindingResult().hasErrors()) {

            errors.addAllErrors(dataBinder.getBindingResult());
        } else {
            try {
                Field campo = Produto.class.getDeclaredField(propertyName);
                campo.setAccessible(true);
                campo.set(produto, propertyValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                errors.rejectValue(propertyName, "invalidProperty", "Não foi possível acessar a propriedade: " + propertyName);
            }
        }
    }


    public List<Produto> BuscarPorNomeEPrecoMenorQue(String nome, double preco){
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThan(nome,preco);
    }
}