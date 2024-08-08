package com.github.jatinde.webflux_payground;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;

import com.github.jatinde.webflux_payground.dto.CustomerDto;
import com.github.jatinde.webflux_payground.dto.OrderDetails;
import com.github.jatinde.webflux_payground.entity.Customer;
import com.github.jatinde.webflux_payground.repositories.CustomerOrderRepository;
import com.github.jatinde.webflux_payground.repositories.CustomerRepository;
import com.github.jatinde.webflux_payground.repositories.ProductRepository;

import reactor.test.StepVerifier;

@SpringBootTest(properties = {
		"repo=repositories",
		// "logging.lovel=org.springframework.r2dbc=DEBUG"
}) 
class WebfluxPaygroundApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(WebfluxPaygroundApplicationTests.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CustomerOrderRepository customerOrderRepository;

	@Autowired
	private DatabaseClient databaseClient;

	@Test
	void contextLoads() {
	}

	@Test
	public void findAll() {
		customerRepository.findAll()
				.map(Customer::to)
				.doOnNext(c -> log.info("{}", c))
				.as(StepVerifier::create)
				.expectNextCount(10)
				.expectComplete()
				.verify();
	}

	@Test
	public void findById() {
		customerRepository.findById(2)
				.doOnNext(c -> log.info("{}", c))
				.as(StepVerifier::create)
				.assertNext(c -> {
					Assertions.assertEquals("mike", c.name());
				})
				.expectComplete()
				.verify();
	}

	@Test
	public void findByName() {
		customerRepository.findByName("jake")
				.doOnNext(c -> log.info("{}", c))
				.as(StepVerifier::create)
				.assertNext(c -> {
					Assertions.assertEquals("jake@gmail.com", c.email());
				})
				.expectComplete()
				.verify();
	}

	@Test
	public void findByEmailWithGmail() {
		customerRepository.findByEmailEndsWith("gmail.com")
				.doOnNext(c -> log.info("{}", c))
				.as(StepVerifier::create)
				.expectNextCount(3)
				.expectComplete()
				.verify();
	}

	@Test
	public void insertAndDelete() {
		var customerdto = new CustomerDto("jat", "jat@kubehub.in");
		log.info("customerdto: {}", customerdto);
		customerRepository.save(Customer.from(customerdto))
				.doOnNext(c -> log.info("{}", c))
				.as(StepVerifier::create)
				.assertNext(c -> Assertions.assertNotNull(c.id()))
				.expectComplete()
				.verify();

		customerRepository.count()
				.doOnNext(c -> {
					log.info("Total Count{}", c);
				})
				.as(StepVerifier::create)
				.expectNext(11L)
				.expectComplete()
				.verify();

		customerRepository.deleteById(11)
				.then(customerRepository.count())
				.as(StepVerifier::create)
				.expectNext(10L)
				.expectComplete()
				.verify();
	}

	@Test
	public void updateCustomer() {

		customerRepository.findByName("ethan")
				.doOnNext(c -> {
					log.info("{}", c);
				}).flatMap(m -> customerRepository.save(new Customer(m.id(), "ruth", m.email())))
				.as(StepVerifier::create)
				.assertNext(c -> {
					Assertions.assertEquals(Integer.valueOf(10), c.id());
					Assertions.assertEquals("ruth", c.name());
				})
				.expectComplete()
				.verify();

	}

	@Test
	public void findProductsRange() {
		productRepository.findAllByPriceBetween(300, 1000)
				.doOnNext(p -> {
					log.info("{}", p);
				})
				.as(StepVerifier::create)
				.assertNext(p -> Assertions.assertEquals(Integer.valueOf(1), p.id()))
				.assertNext(p -> Assertions.assertEquals(Integer.valueOf(2), p.id()))
				.expectNextCount(2L)
				.assertNext(p -> Assertions.assertEquals(Integer.valueOf(10), p.id()))
				.expectComplete()
				.verify();
	}

	@Test
	public void pageableProducts() {
		productRepository.findBy(PageRequest.of(0, 3).withSort(Sort.by("price").descending()))
				.doOnNext(p -> {
					log.info("{}", p);
				})
				.as(StepVerifier::create)
				.expectNextCount(3)
				.expectComplete()
				.verify();
	}

	@Test
	public void pageableProductsLastPage() {
		productRepository.findBy(PageRequest.of(3, 3).withSort(Sort.by("price").descending()))
				.doOnNext(p -> {
					log.info("{}", p);
				})
				.as(StepVerifier::create)
				.expectNextCount(1)
				.expectComplete()
				.verify();
	}

	@Test
	public void getProductsOrderedbyCustomer() {
		customerOrderRepository.getProductsOrderedbyCustomer("mike")
				.doOnNext(p -> {
					log.info("{}", p);
				}).as(StepVerifier::create)
				.assertNext(p -> Assertions.assertEquals(Integer.valueOf(1000), p.price()))
				.assertNext(p -> Assertions.assertEquals(Integer.valueOf(3000), p.price()))
				.expectComplete()
				.verify();
	}

	@Test
	public void findAllOrders() {
		customerOrderRepository.findAll()
				.doOnNext(co -> {
					log.info("{}", co);
				}).as(StepVerifier::create)
				.expectNextCount(6L)
				.expectComplete()
				.verify();
	}

	@Test
	public void getOrderDetailsByProduct() {
		customerOrderRepository.getOrderDetailsByProduct("iphone 20")
				.doOnNext(od -> {
					log.info("{}", od);
				})
				.as(StepVerifier::create)
				.assertNext(od -> Assertions.assertEquals(Integer.valueOf(975), od.amount()))
				.assertNext(od -> Assertions.assertEquals(Integer.valueOf(950), od.amount()))
				.expectComplete()
				.verify();

	}

	@Test
	public void getOrderDetailsByProductByDatabaseClient() {
		var sql = """
				SELECT
				             co.order_id,
				             c.name AS customer_name,
				             p.description AS product_name,
				             co.amount,
				             co.order_date
				         FROM
				             customer c
				         INNER JOIN customer_order co ON c.id = co.customer_id
				         INNER JOIN product p ON p.id = co.product_id
				         WHERE
				             p.description = :description
				         ORDER BY co.amount DESC
				""";

		var orderDetails = this.databaseClient.sql(sql)
				.bind("description", "iphone 20")
				.mapProperties(OrderDetails.class)
				.all();
		orderDetails
				.doOnNext(od -> {
					log.info("{}", od);
				})
				.as(StepVerifier::create)
				.assertNext(od -> Assertions.assertEquals(Integer.valueOf(975), od.amount()))
				.assertNext(od -> Assertions.assertEquals(Integer.valueOf(950), od.amount()))
				.expectComplete()
				.verify();

	}

}
