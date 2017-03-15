package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * shows monthly account statistics, TODO make it nice and DBClass compatible
 * @author konriz
 *
 */
public class MonthReportDialog extends JDialog
{
	
	private ArrayList<OrderList> reportList;
	private Shop shop;
	
	public MonthReportDialog(ShopFrame owner)
	{
		super(owner, "Raport miesięczny", true);
		shop = owner.getShop();
		Report report = new Report(shop);
		reportList = report.getDateOrders();
		JPanel reportPanel = new JPanel();
		JPanel topPanel = new JPanel();
		// TODO 2 x combo box do wyboru dat lub inna forma wyboru - spinner dla dni i lat, combo box dla miesiecy lub raport tylko dla miesięcy
		
		JButton reportButton = new JButton("Pokaż");
		reportButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event)
			{
				
			}
		});
		
		topPanel.add(reportButton);
		add(topPanel, BorderLayout.NORTH);
		JTable table = createTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600,500));
		reportPanel.add(scrollPane, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 2));
		buttonPanel.add(new JLabel("Suma utargu za okres:"));
		buttonPanel.add(new JLabel("" + report.getAccount()));
		buttonPanel.add(new JLabel("Ilość paragonów w okresie:"));
		buttonPanel.add(new JLabel("" + report.getOrdersCount()));
		
		reportPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(reportPanel);
		pack();
		setSize(700,700);
		setLocationRelativeTo(owner);
		
	}
	
	public JTable createTable()
	{
		DefaultTableModel tableModel = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
	
		JTable table = new JTable(tableModel);
		
		String[] columnNames = {"L.P.", "Dzień", " Miesiąc", "Ilość paragonów", "Utarg netto","Brutto gotówka", "Brutto karta", "Utarg brutto"};
		for (String e : columnNames)
		{
		tableModel.addColumn(e);
		}
		
		int lp = 1;
		for (OrderList e : reportList)
		{
			GregorianCalendar open = e.getDateOpen();
			Integer customers = e.getOrdersAmount();
			Double value = e.getOrdersValue();
			double valueB = e.getOrdersValueB();
			double card = e.getOrdersCard();
			double money = e.getOrdersMoney();
			
			Object[] rowToAdd = new Object[]{lp, open.get(Calendar.DAY_OF_MONTH),open.get(Calendar.MONTH) + 1 ,customers, value, money, card, valueB};
			tableModel.addRow(rowToAdd);
			lp ++;
		}
		TableColumnModel tM = table.getColumnModel();
		tM.getColumn(0).setPreferredWidth(5);
		
				
		return table;
	}
}