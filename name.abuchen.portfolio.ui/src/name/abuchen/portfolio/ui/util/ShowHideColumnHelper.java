package name.abuchen.portfolio.ui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

public class ShowHideColumnHelper implements IMenuListener
{
    public static class Column
    {
        private String label;
        private int style;
        private int defaultWidth;
        private boolean isVisible = true;
        private ColumnViewerSorter sorter;
        private CellLabelProvider labelProvider;

        public Column(String label, int style, int defaultWidth)
        {
            this.label = label;
            this.style = style;
            this.defaultWidth = defaultWidth;
        }

        public void setVisible(boolean isVisible)
        {
            this.isVisible = isVisible;
        }

        public void setSorter(ColumnViewerSorter sorter)
        {
            this.sorter = sorter;
        }

        public void setLabelProvider(CellLabelProvider labelProvider)
        {
            this.labelProvider = labelProvider;
        }

        String getLabel()
        {
            return label;
        }

        int getStyle()
        {
            return style;
        }

        int getDefaultWidth()
        {
            return defaultWidth;
        }

        boolean isVisible()
        {
            return isVisible;
        }

        ColumnViewerSorter getSorter()
        {
            return sorter;
        }

        CellLabelProvider getLabelProvider()
        {
            return labelProvider;
        }

        public void create(TableViewer viewer, TableColumnLayout layout)
        {
            TableViewerColumn col = new TableViewerColumn(viewer, getStyle());
            col.getColumn().setText(getLabel());
            col.getColumn().setMoveable(true);
            col.setLabelProvider(getLabelProvider());

            layout.setColumnData(col.getColumn(), new ColumnPixelData(getDefaultWidth()));
            col.getColumn().setWidth(getDefaultWidth());

            if (sorter != null)
                sorter.attachTo(viewer, col);

            col.getColumn().setData(Column.class.getName(), this);
        }

        public void destroy(TableViewer viewer)
        {
            for (TableColumn column : viewer.getTable().getColumns())
            {
                if (column.getData(Column.class.getName()) == this)
                {
                    try
                    {
                        viewer.getTable().setRedraw(false);
                        column.dispose();
                    }
                    finally
                    {
                        viewer.getTable().setRedraw(true);
                    }
                    break;
                }
            }
        }
    }

    private List<Column> columns = new ArrayList<Column>();

    private TableViewer viewer;
    private TableColumnLayout layout;
    private Menu contextMenu;

    public ShowHideColumnHelper(TableViewer viewer, TableColumnLayout layout)
    {
        this.viewer = viewer;
        this.layout = layout;

        this.viewer.getTable().addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                ShowHideColumnHelper.this.widgetDisposed();
            }
        });
    }

    private void widgetDisposed()
    {
        if (contextMenu != null)
            contextMenu.dispose();
    }

    public void showHideShowColumnsMenu(Shell shell)
    {
        if (contextMenu == null)
        {
            MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
            menuMgr.setRemoveAllWhenShown(true);
            menuMgr.addMenuListener(this);

            contextMenu = menuMgr.createContextMenu(shell);
        }

        contextMenu.setVisible(true);
    }

    @Override
    public void menuAboutToShow(IMenuManager manager)
    {
        final Set<Column> visible = new HashSet<Column>();
        for (TableColumn col : viewer.getTable().getColumns())
            visible.add((Column) col.getData(Column.class.getName()));

        for (final Column col : columns)
        {
            Action action = new Action(col.getLabel())
            {
                @Override
                public void run()
                {
                    if (visible.contains(col))
                        col.destroy(viewer);
                    else
                    {
                        col.create(viewer, layout);
                        viewer.refresh(true);
                    }
                }
            };
            action.setChecked(visible.contains(col));
            manager.add(action);
        }
    }

    public void addColumn(Column column)
    {
        columns.add(column);
    }

    public void createColumns()
    {
        for (Column column : columns)
        {
            if (column.isVisible())
                column.create(viewer, layout);
        }
    }

}