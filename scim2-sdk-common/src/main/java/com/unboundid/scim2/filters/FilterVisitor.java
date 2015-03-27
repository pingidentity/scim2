package com.unboundid.scim2.filters;

import com.unboundid.scim2.exceptions.SCIMException;

/**
 * Created by boli on 3/26/15.
 */
public interface FilterVisitor<R, P>
{
  public R visit(EqualFilter filter, P param) throws SCIMException;
  public R visit(NotEqualFilter filter, P param) throws SCIMException;
  public R visit(ContainsFilter filter, P param) throws SCIMException;
  public R visit(StartsWithFilter filter, P param) throws SCIMException;
  public R visit(EndsWithFilter filter, P param) throws SCIMException;
  public R visit(PresentFilter filter, P param) throws SCIMException;
  public R visit(GreaterThanFilter filter, P param) throws SCIMException;
  public R visit(GreaterThanOrEqualFilter filter, P param) throws SCIMException;
  public R visit(LessThanFilter filter, P param) throws SCIMException;
  public R visit(LessThanOrEqualFilter filter, P param) throws SCIMException;
  public R visit(AndFilter filter, P param) throws SCIMException;
  public R visit(OrFilter filter, P param) throws SCIMException;
  public R visit(NotFilter filter, P param) throws SCIMException;
  public R visit(ComplexValueFilter filter, P param) throws SCIMException;
}
