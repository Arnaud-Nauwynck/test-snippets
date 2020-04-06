package fr.an.tests.sortedmergediff;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;

public abstract class DiffEntry<T> {

	public enum DiffEntryType {
		LeftOnly,
		Update,
		Equals,
		RightOnly
	}
	
	public static abstract class DiffEntryVisitor<T> {
		public abstract void caseLeftOnly(LeftOnlyDiffEntry<T> diffEntry);
		public abstract void caseUpdate(UpdateDiffEntry<T> diffEntry);
		public abstract void caseEquals(EqualsDiffEntry<T> diffEntry);
		public abstract void caseRightOnly(RightOnlyDiffEntry<T> diffEntry);
	}
	
	public abstract void accept(DiffEntryVisitor<T> visitor);

	public abstract DiffEntryType getDiffEntryType();
	
	public abstract T forKey();
	
	@Data 
	@AllArgsConstructor
	public static class LeftOnlyDiffEntry<T> extends DiffEntry<T> {
		T left;
		int leftLineNum;

		@Override
		public void accept(DiffEntryVisitor<T> visitor) {
			visitor.caseLeftOnly(this);
		}
		
		@Override
		public DiffEntryType getDiffEntryType() {
			return DiffEntryType.LeftOnly;
		}

		@Override
		public T forKey() {
			return left;
		}
		public <K> K extractKey(Function<T,K> keyExtract) {
			return keyExtract.apply(left);
		}
	}
	
	@Data 
	@AllArgsConstructor
	public static class UpdateDiffEntry<T> extends DiffEntry<T> {
		T left;
		int leftLineNum;
		T right;
		int rightLineNum;

		@Override
		public void accept(DiffEntryVisitor<T> visitor) {
			visitor.caseUpdate(this);
		}

		@Override
		public DiffEntryType getDiffEntryType() {
			return DiffEntryType.Update;
		}

		@Override
		public T forKey() {
			return left;
		}

	}

	@Data 
	@AllArgsConstructor
	public static class EqualsDiffEntry<T> extends DiffEntry<T> {
		T left;
		int leftLineNum;
		T right;
		int rightLineNum;
		
		@Override
		public void accept(DiffEntryVisitor<T> visitor) {
			visitor.caseEquals(this);
		}

		@Override
		public DiffEntryType getDiffEntryType() {
			return DiffEntryType.Equals;
		}

		@Override
		public T forKey() {
			return left;
		}

	}

	@Data 
	@AllArgsConstructor
	public static class RightOnlyDiffEntry<T>  extends DiffEntry<T> {
		T right;
		int rightLineNum;
		
		@Override
		public void accept(DiffEntryVisitor<T> visitor) {
			visitor.caseRightOnly(this);
		}

		@Override
		public DiffEntryType getDiffEntryType() {
			return DiffEntryType.RightOnly;
		}

		@Override
		public T forKey() {
			return right;
		}

	}
	
}