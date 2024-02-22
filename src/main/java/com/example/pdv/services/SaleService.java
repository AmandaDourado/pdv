package com.example.pdv.services;

import com.example.pdv.dto.ProductSaleDTO;
import com.example.pdv.dto.ProductInfoDTO;
import com.example.pdv.dto.SaleDTO;
import com.example.pdv.dto.SaleInfoDTO;
import com.example.pdv.entity.ItemSale;
import com.example.pdv.entity.Product;
import com.example.pdv.entity.Sale;
import com.example.pdv.entity.User;
import com.example.pdv.exceptions.InvalidOperationException;
import com.example.pdv.exceptions.NoItemException;
import com.example.pdv.repository.ItemSaleRepository;
import com.example.pdv.repository.ProductRepository;
import com.example.pdv.repository.SaleRepository;
import com.example.pdv.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ItemSaleRepository itemSaleRepository;

    public SaleService(@Autowired UserRepository userRepository, @Autowired ProductRepository productRepository, @Autowired SaleRepository saleRepository, @Autowired ItemSaleRepository itemSaleRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.itemSaleRepository = itemSaleRepository;
    }

    public List<SaleInfoDTO> findAll(){
        return saleRepository.findAll().stream().map(sale -> getSaleInfo(sale)).collect(Collectors.toList());
    }


    private SaleInfoDTO getSaleInfo(Sale sale) {
        var products = getProductInfo(sale.getItems());
        BigDecimal total = getTotal(products);

        SaleInfoDTO saleInfoDTO = new SaleInfoDTO();
        saleInfoDTO.setUser(sale.getUser().getName());
        saleInfoDTO.setDate(sale.getDate().format(DateTimeFormatter.ofPattern("dd//MM/yyyy")));
        saleInfoDTO.setProducts(products);
        saleInfoDTO.setTotal(total);
        saleInfoDTO.setTotal(total);

        return saleInfoDTO;
    }

    private BigDecimal getTotal(List<ProductInfoDTO> products) {
        BigDecimal total = new BigDecimal(0);
        for(int i = 0; i < products.size(); i++){
            total = total.add(products.get(i).getPrice().multiply(new BigDecimal(products.get(i).getQuantity())));
        }
        return total;
    }

    private List<ProductInfoDTO> getProductInfo(List<ItemSale> items) {
        return items.stream().map(item -> {
            ProductInfoDTO productInfoDTO = new ProductInfoDTO();
            productInfoDTO.setId(item.getId());
            productInfoDTO.setDescription(item.getProduct().getDescription());
            productInfoDTO.setQuantity(item.getQuantity());
            productInfoDTO.setPrice(item.getProduct().getPrice());
            return productInfoDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    public long save(SaleDTO saleDTO){
        User user = userRepository.findById(saleDTO.getUserid()).orElseThrow(() -> new NoItemException("Usuário não encontrado"));

        Sale newSale = new Sale();
        newSale.setUser(user);
        newSale.setDate(LocalDate.now());
        List<ItemSale> itemSales = getItemSale(saleDTO.getItems());

        newSale = saleRepository.save(newSale);
        saveItemSale(itemSales,newSale);

        return newSale.getId();
    }

    private void saveItemSale(List<ItemSale> itemSales, Sale newSale) {
        for (ItemSale itemSale : itemSales){
            itemSale.setSale(newSale);
            itemSaleRepository.save(itemSale);
        }
    }

    private List<ItemSale> getItemSale(List<ProductSaleDTO> products) {
        if(products.isEmpty()){
            throw new InvalidOperationException("Não é possível adicionar a venda sem itens");
        }

        return products.stream().map(item -> {
            Product product = productRepository.findById(item.getProductid()).
                    orElseThrow(() -> new NoItemException("Produto não encontrado"));

            ItemSale itemSale = new ItemSale();
            itemSale.setProduct(product);
            itemSale.setQuantity(item.getQuantity());

            if(product.getQuantity() == 0){
                throw new NoItemException("Produto sem estoque");
            } else if (product.getQuantity() < item.getQuantity()) {
                throw new InvalidOperationException(String.format("A quantidade de itens da venda (%s) " +
                        "é maior do que a quantidade disponivel no estoque (%s)",item.getQuantity(),product.getQuantity()));
            }

            int total = product.getQuantity() - item.getQuantity();
            product.setQuantity(total);
            productRepository.save(product);

            return itemSale;
        }).collect(Collectors.toList());
    }

    public SaleInfoDTO getById(long id) {
       Sale sale = saleRepository.findById(id).orElseThrow(() -> new NoItemException("Venda não encontrada"));
       return getSaleInfo(sale);
    }
}
