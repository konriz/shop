package pl.konriz.shop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * shop's menu bar; main way to run most of the functionalities TODO think about hotkeys
 * @author konriz
 *
 */
public class ShopMenu extends JMenuBar {

	private Shop shop;
	private TxtField eanField;
	private ShopFrame owner;
	
	public ShopFrame getOwner()
	{
		return owner;
	}
	
	public ShopMenu(ShopFrame own)
	{
		owner = own;
		shop = owner.getShop();
		eanField = owner.getEanField();
		JMenu menuShop = new JMenu("Sklep");
		JMenuItem openShop = new JMenuItem("Otwórz");
		openShop.addActionListener(new OpenShopAction());
		openShop.setEnabled(false);
		
		JMenuItem closeShop = new JMenuItem("Zamknij");
	
		closeShop.addActionListener(new CloseShopAction());
		
		menuShop.add(openShop);
		menuShop.add(closeShop);
		add(menuShop);
		
		JMenu menuStock = new JMenu ("Towar");
		JMenuItem stockList = new JMenuItem("Magazyn");
		stockList.addActionListener(new StockViewAction(owner));
		JMenuItem invoiceAdd = new JMenuItem("Dodaj FV");
		invoiceAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event)
			{
				InvoiceAddDialog addDialog = new InvoiceAddDialog(owner);
				addDialog.setVisible(true);
			}
		});
		
		JMenuItem invoiceShow = new JMenuItem("Pokaż FV");
		invoiceShow.addActionListener(new InvoiceViewAction(owner));
		
		menuStock.add(stockList);
		menuStock.addSeparator();
		menuStock.add(invoiceAdd);
		menuStock.add(invoiceShow);
		add(menuStock);
		
		JMenu menuReport = new JMenu ("Raporty");
		JMenuItem reportDay = new JMenuItem("Dzisiaj");
		reportDay.addActionListener(new DayReportAction(owner));
		JMenuItem reportMonth = new JMenuItem("Miesiąc");		
		reportMonth.addActionListener(new MonthReportAction(owner));
		menuReport.add(reportDay);
		menuReport.add(reportMonth);
		add(menuReport);
		}
		
		
		
		class OpenShopAction extends AbstractAction
		{
			public void actionPerformed(ActionEvent event)
			{			
				if (!shop.isOpen())
				{
					getMenu(0).getItem(0).setEnabled(false);
					getMenu(0).getItem(1).setEnabled(true);
					getMenu(2).getItem(0).setEnabled(true);
					getMenu(2).getItem(1).setEnabled(true);
					JButton addItemButton = owner.getAddItemButton();
					addItemButton.setEnabled(true);
					JButton payButton = owner.getPayButton();
					payButton.setEnabled(true);
					shop.openShop();
					eanField.grabFocus();
				}			
			}
		}
		
		class CloseShopAction extends AbstractAction
		{
			public CloseShopAction()
			{
				putValue(Action.SHORT_DESCRIPTION, "Zamknij sklep - zakończ sprzedaż.");
			}
			
			public void actionPerformed(ActionEvent event)
			{
				if (shop.isOpen())
				{
					getMenu(0).getItem(0).setEnabled(true);
					getMenu(0).getItem(1).setEnabled(false);
					getMenu(2).getItem(0).setEnabled(true);
					JButton addItemButton = owner.getAddItemButton();
					addItemButton.setEnabled(false);
					JButton payButton = owner.getPayButton();
					payButton.setEnabled(false);
					shop.closeShop();
					
				}				
			}
		}
		
		class StockViewAction extends AbstractAction
		{		
			private ShopFrame owner;
			
			public StockViewAction(ShopFrame own)
			{
				owner = own;
			}
			
			public void actionPerformed(ActionEvent event)
			{			
				StockViewDialog stockDialog = new StockViewDialog(owner);
				stockDialog.setVisible(true);
			}
		}
		
		class InvoiceViewAction extends AbstractAction
		{		
			private ShopFrame owner;
			
			public InvoiceViewAction(ShopFrame own)
			{
				owner = own;
			}
			
			public void actionPerformed(ActionEvent event)
			{			
				InvoiceViewDialog invoiceViewDialog = new InvoiceViewDialog(owner);
				invoiceViewDialog.setVisible(true);
			}
		}
		
		class DayReportAction extends AbstractAction
		{
			private ShopFrame owner;
			
			public DayReportAction(ShopFrame own)
			{
				owner = own;
			}
			
			public void actionPerformed(ActionEvent event)
			{
				DayReportDialog reportDialog = new DayReportDialog(owner);
				reportDialog.setVisible(true);
			}
		}

		class MonthReportAction extends AbstractAction
		{
			private ShopFrame owner;
			
			public MonthReportAction(ShopFrame own)
			{
				owner = own;
			}
			
			public void actionPerformed(ActionEvent event)
			{
				MonthReportDialog reportDialog = new MonthReportDialog(owner);
				reportDialog.setVisible(true);
			}
		}
}
