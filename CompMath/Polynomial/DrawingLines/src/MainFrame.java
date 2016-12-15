import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class MainFrame extends JFrame
{
	Plane plane;
	GrahpicsPanel gp;
	public JList lst;

	public MainFrame()
	{
		initFrame();
	}

	public void initFrame()
	{

		this.setTitle("Interpolation");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null); //Open window in center of screen
		this.setVisible(true);
		this.setLayout(new BorderLayout());

		plane = new Plane();
		gp = new GrahpicsPanel(plane);

		lst = new JList(new String[]{"y = sin(x)", "y = x + sin(x)", "y = (1/4)^x"});

		JPanel pnlRight = new JPanel();
		Button btn = new Button("Get answer");
		Button btnClear = new Button("Clear");
		JSpinner jsp = new JSpinner();
		Label lbl = new Label();
		lbl.setText("Res: ");

		this.add(pnlRight, BorderLayout.EAST);
		this.add(gp, BorderLayout.CENTER);
		pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.PAGE_AXIS));
		pnlRight.add(lst);
		pnlRight.add(jsp);
		pnlRight.add(lbl);
		pnlRight.add(btn);
		pnlRight.add(btnClear);

		gp.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				Point coursor = e.getPoint();
				System.out.println("Component coords: x = " + coursor.getX() + ", y = " + coursor.getY());

				//Преобразование координат
				Point center = new Point(gp.getWidth() / 2, gp.getHeight() / 2);
				Point selectedPoint = new Point((int) coursor.getX() - (int) center.getX(), -((int) coursor.getY() - (int) center.getY()));

				//Создаем новый круг
				Circle goodCircle = new Circle(coursor, 20);

				//Создаем новый поток
				ThreadCircle threadCircle = new ThreadCircle(gp, goodCircle); //!
				threadCircle.start(); //?!

				double x = selectedPoint.getX()/gp.getUnit();

				boolean isInCollection = false;
				for (int i = 0; i < plane.xPointVector.size(); i++)
				{
					if(plane.xPointVector.get(i) == x)
						isInCollection = true;
				}

				gp.addNewCircle(goodCircle);

				//Добавляю точку x
				if(!isInCollection)
					plane.xPointVector.add(x);

				System.out.println("Component coords: x = " + selectedPoint.getX()/gp.getUnit() + ", y = " + selectedPoint.getY()/gp.getUnit());
			}
		});

		lst.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				plane.num = lst.getSelectedIndex();
				gp.repaint();
			}
		});

		btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int x = (int) jsp.getValue();
				double y = 0;

				Vector<Double> pointVector = plane.xPointVector;

				for (int k = 0; k < pointVector.size(); k++)
				{
					double p = 1.0;

					for (int j = 0; j < pointVector.size(); j++)
					{
						if (j != k)
						{
							p *= x - pointVector.get(j);
							p /= pointVector.get(k) - pointVector.get(j);
						}
					}

					y += p*(Functions.func(pointVector.get(k), plane.num)); //f(x_k)
				}

				lbl.setText("y = " + y);
			}
		});

		btnClear.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				plane.xPointVector.clear();
				gp.repaint();
			}
		});

		this.setSize(600, 530);
	}
}
