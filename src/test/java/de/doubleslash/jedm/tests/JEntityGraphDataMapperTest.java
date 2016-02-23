package de.doubleslash.jedm.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.doubleslash.jedm.generate.EntityGraphs;
import de.doubleslash.jedm.generate.FilePaths;
import de.doubleslash.jedm.generate.JEntityGraphDataMapper;
import de.doubleslash.jedm.model.businessentities.AddressBE;
import de.doubleslash.jedm.model.businessentities.BillBE;
import de.doubleslash.jedm.model.businessentities.CustomerBE;
import de.doubleslash.jedm.model.businessentities.ItemBE;
import de.doubleslash.jedm.model.dataobjects.AddressDO;
import de.doubleslash.jedm.model.dataobjects.BillDO;
import de.doubleslash.jedm.model.dataobjects.CustomerDO;
import de.doubleslash.jedm.model.dataobjects.ItemDO;

public class JEntityGraphDataMapperTest {
	private EntityManagerFactory emf;
	private EntityManager em;
	private JEntityGraphDataMapper jedm;

	/*
	 * Creates a new CustomerBE-Object and persists it to the data base
	 */
	private CustomerBE writeCustomerToDB(final EntityManager em, final String fName, final String lName,
			final int age) {
		em.getTransaction().begin();
		final CustomerBE customer = new CustomerBE(fName, lName, age);
		em.persist(customer);
		em.getTransaction().commit();
		return customer;
	}

	/*
	 * Creates a new AddressBE-Object, adds it to the customer and persists it
	 * to the data base
	 */
	private AddressBE writeAddressToDB(final EntityManager em, final CustomerBE customer, final String street,
			final int number, final String town, final int zip) {
		em.getTransaction().begin();
		final AddressBE address = new AddressBE(street, number, town, zip);
		customer.getAddresses().add(address);
		em.persist(address);
		em.getTransaction().commit();
		return address;
	}

	/*
	 * Creates a new BillBE-Object, adds it to the customer and persists it to
	 * the data base
	 */
	private BillBE writeBillToDB(final EntityManager em, final CustomerBE customer, final Date date) {
		em.getTransaction().begin();
		final BillBE bill = new BillBE(date);
		customer.getBills().add(bill);
		em.persist(bill);
		em.getTransaction().commit();
		return bill;
	}

	/*
	 * Creates a new ItemBE-Object, adds it to the bill and persists it to the
	 * data base
	 */
	private ItemBE writeItemToDB(final EntityManager em, final BillBE bill, final String descr, final int price,
			final int quantity) {
		em.getTransaction().begin();
		final ItemBE item = new ItemBE(descr, price, quantity);
		bill.getItems().add(item);
		bill.setSum(bill.getSum() + price * quantity);
		em.persist(item);
		em.getTransaction().commit();
		return item;
	}

	/*
	 * Creates a single customer object with hard coded addresses, bills and
	 * items for testing
	 */
	private void initialDB(final EntityManager em) {
		final int numberBills = 4;

		final BillBE[] billArr = new BillBE[numberBills];

		final CustomerBE testCustomer = writeCustomerToDB(em, "Thomas", "Kaiser", 27);

		writeAddressToDB(em, testCustomer, "Poststraße", 1, "München", 80331);
		writeAddressToDB(em, testCustomer, "Tulpenweg", 2, "Hamburg", 20095);
		writeAddressToDB(em, testCustomer, "Am Berg", 3, "Kaufbeuren", 87600);

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2015, 4, 21, 0, 0, 0);
		Date billDate = cal.getTime();

		for (int i = 0; i < numberBills; ++i) {
			billArr[i] = writeBillToDB(em, testCustomer, billDate);
			billDate = DateUtils.addDays(billDate, 1); // to get different dates
		}

		writeItemToDB(em, billArr[0], "8 GB Arbeitsspeicher", 42, 1);
		writeItemToDB(em, billArr[0], "500 W Netzteil", 75, 1);
		writeItemToDB(em, billArr[0], "Grafikkarte", 247, 1);

		writeItemToDB(em, billArr[1], "Router", 142, 1);
		writeItemToDB(em, billArr[1], "Mauspad", 19, 3);
		writeItemToDB(em, billArr[1], "Gaming Maus", 89, 3);
		writeItemToDB(em, billArr[1], "Headset", 59, 2);

		writeItemToDB(em, billArr[2], "256 GB SSD", 109, 2);

		writeItemToDB(em, billArr[3], "USB Tastatur", 49, 2);
		writeItemToDB(em, billArr[3], "27 Zoll Monitor", 219, 1);
		writeItemToDB(em, billArr[3], "24 Zoll Monitor", 139, 2);
	}

	/**
	 * Opens entity manager and initializes all objects with some default values
	 * and connects them.
	 */
	@Before
	public void setUp() {
		emf = Persistence.createEntityManagerFactory("myEntityManager");
		em = emf.createEntityManager();

		jedm = new JEntityGraphDataMapper();

		initialDB(em);
	}

	/**
	 * Closes entity manager.
	 */
	@After
	public void tearDown() {
		em.close();
		emf.close();
	}

	/**
	 * Tests for an existing path to the file.
	 */
	@Test
	public void testXMLFileExists() {

		URL centralMappingFilePath = JEntityGraphDataMapper.class.getResource(FilePaths.CENTRAL_XML_NAME.path());
		File centralRules;
		try {
			centralRules = new File(centralMappingFilePath.toURI());
		} catch (URISyntaxException e1) {
			throw new RuntimeException("Could not load mapping rules");
		}

		assertTrue(centralRules.exists());
	}

	/**
	 * Tests for a not existing path.
	 */
	@Test
	public void testXMLFileNotExists() {
		final String path = "";
		final File centralRules = new File(path);

		assertFalse(centralRules.exists());
	}

	/**
	 * Tests for correct request of a customer object.
	 */
	@Test
	public void testJEntityGraphDataMapperAPIwithCustomerUltraLightOnSuccess() {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + "Test: "
				+ "CustomerUltraLight\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		final CustomerDO cusDO = jedm.useJEntityGraphDataMapper(em, CustomerBE.class, CustomerDO.class,
				EntityGraphs.CUSTOMER_ULTRA_LIGHT.graphName(), 1);

		assertEquals(1, cusDO.getCustomerId());
		assertEquals("Thomas", cusDO.getGivenName());
		assertEquals("Kaiser", cusDO.getFamilyName());
		assertEquals(27, cusDO.getAge());

		assertTrue(cusDO.getAddresses().isEmpty());
		assertTrue(cusDO.getBills().isEmpty());

		// This SYSO is for optical checking the result on the console
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + cusDO
				+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	}

	/**
	 * Tests for correct request of a customer object with all addresses.
	 */
	@Test
	public void testJEntityGraphDataMapperAPIwithCustomerLightOnSuccess() {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + "Test: "
				+ "CustomerLight\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		final CustomerDO cusDO = jedm.useJEntityGraphDataMapper(em, CustomerBE.class, CustomerDO.class,
				EntityGraphs.CUSTOMER_LIGHT.graphName(), 1);

		assertEquals(1, cusDO.getCustomerId());
		assertEquals("Thomas", cusDO.getGivenName());
		assertEquals("Kaiser", cusDO.getFamilyName());
		assertEquals(27, cusDO.getAge());

		final List<AddressDO> addressesSortedByID = new ArrayList<AddressDO>(cusDO.getAddresses());

		Collections.sort(addressesSortedByID, new Comparator<AddressDO>() {
			@Override
			public int compare(final AddressDO a1, final AddressDO a2) {
				return a1.getAddressId() - a2.getAddressId();
			}
		});

		final AddressDO adr1DO = addressesSortedByID.get(0);
		final AddressDO adr2DO = addressesSortedByID.get(1);
		final AddressDO adr3DO = addressesSortedByID.get(2);

		assertEquals(2, adr1DO.getAddressId());
		assertEquals("Poststraße", adr1DO.getStreet());
		assertEquals(1, adr1DO.getHouseNumber());
		assertEquals("München", adr1DO.getTown());
		assertEquals(80331, adr1DO.getZipCode());
		assertEquals(3, adr2DO.getAddressId());
		assertEquals("Tulpenweg", adr2DO.getStreet());
		assertEquals(2, adr2DO.getHouseNumber());
		assertEquals("Hamburg", adr2DO.getTown());
		assertEquals(20095, adr2DO.getZipCode());
		assertEquals(4, adr3DO.getAddressId());
		assertEquals("Am Berg", adr3DO.getStreet());
		assertEquals(3, adr3DO.getHouseNumber());
		assertEquals("Kaufbeuren", adr3DO.getTown());
		assertEquals(87600, adr3DO.getZipCode());

		assertTrue(cusDO.getBills().isEmpty());

		// This SYSO is for optical checking the result on the console
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + cusDO
				+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	}

	/**
	 * Tests for correct request of a customer object with all addresses and
	 * bills.
	 */
	@Test
	public void testJEntityGraphDataMapperAPIwithCustomerBasicOnSuccess() {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + "Test: "
				+ "CustomerBasic\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		final CustomerDO cusDO = jedm.useJEntityGraphDataMapper(em, CustomerBE.class, CustomerDO.class,
				EntityGraphs.CUSTOMER_BASIC.graphName(), 1);

		assertEquals(1, cusDO.getCustomerId());
		assertEquals("Thomas", cusDO.getGivenName());
		assertEquals("Kaiser", cusDO.getFamilyName());
		assertEquals(27, cusDO.getAge());

		final List<AddressDO> addressesSortedByID = new ArrayList<AddressDO>(cusDO.getAddresses());

		Collections.sort(addressesSortedByID, new Comparator<AddressDO>() {
			@Override
			public int compare(final AddressDO a1, final AddressDO a2) {
				return a1.getAddressId() - a2.getAddressId();
			}
		});

		final AddressDO adr1DO = addressesSortedByID.get(0);
		final AddressDO adr2DO = addressesSortedByID.get(1);
		final AddressDO adr3DO = addressesSortedByID.get(2);

		assertEquals(2, adr1DO.getAddressId());
		assertEquals("Poststraße", adr1DO.getStreet());
		assertEquals(1, adr1DO.getHouseNumber());
		assertEquals("München", adr1DO.getTown());
		assertEquals(80331, adr1DO.getZipCode());
		assertEquals(3, adr2DO.getAddressId());
		assertEquals("Tulpenweg", adr2DO.getStreet());
		assertEquals(2, adr2DO.getHouseNumber());
		assertEquals("Hamburg", adr2DO.getTown());
		assertEquals(20095, adr2DO.getZipCode());
		assertEquals(4, adr3DO.getAddressId());
		assertEquals("Am Berg", adr3DO.getStreet());
		assertEquals(3, adr3DO.getHouseNumber());
		assertEquals("Kaufbeuren", adr3DO.getTown());
		assertEquals(87600, adr3DO.getZipCode());

		final List<BillDO> billsSortedByID = new ArrayList<BillDO>(cusDO.getBills());

		Collections.sort(billsSortedByID, new Comparator<BillDO>() {
			@Override
			public int compare(final BillDO b1, final BillDO b2) {
				return b1.getBillId() - b2.getBillId();
			}
		});

		final BillDO bil1DO = billsSortedByID.get(0);
		final BillDO bil2DO = billsSortedByID.get(1);
		final BillDO bil3DO = billsSortedByID.get(2);
		final BillDO bil4DO = billsSortedByID.get(3);

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2015, 4, 21, 0, 0, 0);
		final Date date1 = cal.getTime();
		final Date date2 = DateUtils.addDays(date1, 1);
		final Date date3 = DateUtils.addDays(date2, 1);
		final Date date4 = DateUtils.addDays(date3, 1);

		assertEquals(5, bil1DO.getBillId());
		assertEquals(date1, bil1DO.getBillDate());
		assertEquals(364, bil1DO.getTotal());
		assertEquals(6, bil2DO.getBillId());
		assertEquals(date2, bil2DO.getBillDate());
		assertEquals(584, bil2DO.getTotal());
		assertEquals(7, bil3DO.getBillId());
		assertEquals(date3, bil3DO.getBillDate());
		assertEquals(218, bil3DO.getTotal());
		assertEquals(8, bil4DO.getBillId());
		assertEquals(date4, bil4DO.getBillDate());
		assertEquals(595, bil4DO.getTotal());

		assertTrue(cusDO.getBills().get(0).getItems().isEmpty());
		assertTrue(cusDO.getBills().get(1).getItems().isEmpty());
		assertTrue(cusDO.getBills().get(2).getItems().isEmpty());
		assertTrue(cusDO.getBills().get(3).getItems().isEmpty());

		// This SYSO is for optical checking the result on the console
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + cusDO
				+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	}

	/**
	 * Tests for correct request of a customer object with all addresses, bills
	 * and items.
	 */
	@Test
	public void testJEntityGraphDataMapperAPIwithCustomerFullOnSuccess() {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + "Test: "
				+ "CustomerFull\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		final CustomerDO cusDO = jedm.useJEntityGraphDataMapper(em, CustomerBE.class, CustomerDO.class,
				EntityGraphs.CUSTOMER_FULL.graphName(), 1);

		assertEquals(1, cusDO.getCustomerId());
		assertEquals("Thomas", cusDO.getGivenName());
		assertEquals("Kaiser", cusDO.getFamilyName());
		assertEquals(27, cusDO.getAge());

		final List<AddressDO> addressesSortedByID = new ArrayList<AddressDO>(cusDO.getAddresses());

		Collections.sort(addressesSortedByID, new Comparator<AddressDO>() {
			@Override
			public int compare(final AddressDO a1, final AddressDO a2) {
				return a1.getAddressId() - a2.getAddressId();
			}
		});

		final AddressDO adr1DO = addressesSortedByID.get(0);
		final AddressDO adr2DO = addressesSortedByID.get(1);
		final AddressDO adr3DO = addressesSortedByID.get(2);

		assertEquals(2, adr1DO.getAddressId());
		assertEquals("Poststraße", adr1DO.getStreet());
		assertEquals(1, adr1DO.getHouseNumber());
		assertEquals("München", adr1DO.getTown());
		assertEquals(80331, adr1DO.getZipCode());
		assertEquals(3, adr2DO.getAddressId());
		assertEquals("Tulpenweg", adr2DO.getStreet());
		assertEquals(2, adr2DO.getHouseNumber());
		assertEquals("Hamburg", adr2DO.getTown());
		assertEquals(20095, adr2DO.getZipCode());
		assertEquals(4, adr3DO.getAddressId());
		assertEquals("Am Berg", adr3DO.getStreet());
		assertEquals(3, adr3DO.getHouseNumber());
		assertEquals("Kaufbeuren", adr3DO.getTown());
		assertEquals(87600, adr3DO.getZipCode());

		final List<BillDO> billsSortedByID = new ArrayList<BillDO>(cusDO.getBills());

		Collections.sort(billsSortedByID, new Comparator<BillDO>() {
			@Override
			public int compare(final BillDO b1, final BillDO b2) {
				return b1.getBillId() - b2.getBillId();
			}
		});

		final BillDO bil1DO = billsSortedByID.get(0);
		final BillDO bil2DO = billsSortedByID.get(1);
		final BillDO bil3DO = billsSortedByID.get(2);
		final BillDO bil4DO = billsSortedByID.get(3);

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2015, 4, 21, 0, 0, 0);
		final Date date1 = cal.getTime();
		final Date date2 = DateUtils.addDays(date1, 1);
		final Date date3 = DateUtils.addDays(date2, 1);
		final Date date4 = DateUtils.addDays(date3, 1);

		assertEquals(5, bil1DO.getBillId());
		assertEquals(date1, bil1DO.getBillDate());
		assertEquals(364, bil1DO.getTotal());
		assertEquals(6, bil2DO.getBillId());
		assertEquals(date2, bil2DO.getBillDate());
		assertEquals(584, bil2DO.getTotal());
		assertEquals(7, bil3DO.getBillId());
		assertEquals(date3, bil3DO.getBillDate());
		assertEquals(218, bil3DO.getTotal());
		assertEquals(8, bil4DO.getBillId());
		assertEquals(date4, bil4DO.getBillDate());
		assertEquals(595, bil4DO.getTotal());

		// Items of first bill
		List<ItemDO> itemsSortedByID = new ArrayList<ItemDO>(bil1DO.getItems());

		Collections.sort(itemsSortedByID, new Comparator<ItemDO>() {
			@Override
			public int compare(final ItemDO i1, final ItemDO i2) {
				return i1.getItemId() - i2.getItemId();
			}
		});

		final ItemDO ite1DO = itemsSortedByID.get(0);
		final ItemDO ite2DO = itemsSortedByID.get(1);
		final ItemDO ite3DO = itemsSortedByID.get(2);

		// Items of second bill
		itemsSortedByID = new ArrayList<ItemDO>(bil2DO.getItems());

		Collections.sort(itemsSortedByID, new Comparator<ItemDO>() {
			@Override
			public int compare(final ItemDO i1, final ItemDO i2) {
				return i1.getItemId() - i2.getItemId();
			}
		});

		final ItemDO ite4DO = itemsSortedByID.get(0);
		final ItemDO ite5DO = itemsSortedByID.get(1);
		final ItemDO ite6DO = itemsSortedByID.get(2);
		final ItemDO ite7DO = itemsSortedByID.get(3);

		// Item of third bill
		final ItemDO ite8DO = bil3DO.getItems().get(0);

		// Items of fourth bill
		itemsSortedByID = new ArrayList<ItemDO>(bil4DO.getItems());

		Collections.sort(itemsSortedByID, new Comparator<ItemDO>() {
			@Override
			public int compare(final ItemDO i1, final ItemDO i2) {
				return i1.getItemId() - i2.getItemId();
			}
		});

		final ItemDO ite9DO = itemsSortedByID.get(0);
		final ItemDO ite10DO = itemsSortedByID.get(1);
		final ItemDO ite11DO = itemsSortedByID.get(2);

		assertEquals(9, ite1DO.getItemId());
		assertEquals("8 GB Arbeitsspeicher", ite1DO.getLabel());
		assertEquals(42, ite1DO.getSinglePrice());
		assertEquals(1, ite1DO.getQuantity());
		assertEquals(10, ite2DO.getItemId());
		assertEquals("500 W Netzteil", ite2DO.getLabel());
		assertEquals(75, ite2DO.getSinglePrice());
		assertEquals(1, ite2DO.getQuantity());
		assertEquals(11, ite3DO.getItemId());
		assertEquals("Grafikkarte", ite3DO.getLabel());
		assertEquals(247, ite3DO.getSinglePrice());
		assertEquals(1, ite3DO.getQuantity());
		assertEquals(12, ite4DO.getItemId());
		assertEquals("Router", ite4DO.getLabel());
		assertEquals(142, ite4DO.getSinglePrice());
		assertEquals(1, ite4DO.getQuantity());
		assertEquals(13, ite5DO.getItemId());
		assertEquals("Mauspad", ite5DO.getLabel());
		assertEquals(19, ite5DO.getSinglePrice());
		assertEquals(3, ite5DO.getQuantity());
		assertEquals(14, ite6DO.getItemId());
		assertEquals("Gaming Maus", ite6DO.getLabel());
		assertEquals(89, ite6DO.getSinglePrice());
		assertEquals(3, ite6DO.getQuantity());
		assertEquals(15, ite7DO.getItemId());
		assertEquals("Headset", ite7DO.getLabel());
		assertEquals(59, ite7DO.getSinglePrice());
		assertEquals(2, ite7DO.getQuantity());
		assertEquals(16, ite8DO.getItemId());
		assertEquals("256 GB SSD", ite8DO.getLabel());
		assertEquals(109, ite8DO.getSinglePrice());
		assertEquals(2, ite8DO.getQuantity());
		assertEquals(17, ite9DO.getItemId());
		assertEquals("USB Tastatur", ite9DO.getLabel());
		assertEquals(49, ite9DO.getSinglePrice());
		assertEquals(2, ite9DO.getQuantity());
		assertEquals(18, ite10DO.getItemId());
		assertEquals("27 Zoll Monitor", ite10DO.getLabel());
		assertEquals(219, ite10DO.getSinglePrice());
		assertEquals(1, ite10DO.getQuantity());
		assertEquals(19, ite11DO.getItemId());
		assertEquals("24 Zoll Monitor", ite11DO.getLabel());
		assertEquals(139, ite11DO.getSinglePrice());
		assertEquals(2, ite11DO.getQuantity());

		// This SYSO is for optical checking the result on the console
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + cusDO
				+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	}
}