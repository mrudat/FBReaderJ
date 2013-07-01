package org.geometerplus.fbreader.widget;

import java.util.List;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.IBookCollection;
import org.geometerplus.fbreader.book.XMLSerializer;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class ChooseBookActivity extends ListActivity implements BooksModel.Listener {
	private final IBookCollection myCollection = new BookCollectionShadow();
	private BooksModel myBooksModel = new BooksModel();
	private List<Book> myBooks;
	private BooksAdapter myBooksAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myBooksModel.addListener(this);
		((BookCollectionShadow)myCollection).bindToService(this, new Runnable() {
			@Override
			public void run() {
				myBooksModel.initialize(myCollection);
				myBooks = myBooksModel.getBooks();
				myBooksAdapter = new BooksAdapter(myBooks, getListView());
				setListAdapter(myBooksAdapter);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		myBooksModel.removeListener(this);
		((BookCollectionShadow)myCollection).unbind();
		super.onDestroy();	
	}

	public List<Book> getBooks() {
		return myBooks;
	}

	@Override
	public void update() {
		myBooksAdapter.notifyDataSetChanged();
	}
	
	private final class BooksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
		private final List<Book> myBooks;
		
		BooksAdapter(List<Book> books, ListView listView) {
			myBooks = books;
			listView.setAdapter(this);
			listView.setOnItemClickListener(this);
		}

		@Override
		public int getCount() {
			return myBooks.size();
		}

		@Override
		public Book getItem(int position) {
			return myBooks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Book book = getItem(position);
			final View view = (convertView != null) ? convertView :
				LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
			final TwoLineListItem tlli = (TwoLineListItem)view.findViewById(R.id.book_item);
			tlli.getText1().setText(book.getTitle());
			if (!book.authors().isEmpty()) {
				tlli.getText2().setText(book.authors().get(0).DisplayName);
			} else {
				tlli.getText2().setText(" ");
			}
			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final Book book = getItem(position);
			final Intent resultValue = new Intent();
			resultValue.putExtra("book", new XMLSerializer().serialize(book));
			setResult(RESULT_OK, resultValue);
			finish();
		}
	}
}
