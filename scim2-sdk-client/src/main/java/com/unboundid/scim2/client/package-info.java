/*
 * Copyright 2015-2018 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */

/**
 * Client classes for accessing SCIM 2 resources. SCIM 2 clients are typically
 * built around the {@link com.unboundid.scim2.client.ScimService} class,
 * which provides methods for accessing a SCIM 2 service provider's resources,
 * configuration, schemas, and resource types. Requests for resources may
 * designate the type of the return value by specifying a
 * {@link com.unboundid.scim2.common.GenericScimResource} or a POJO derived
 * from {@link com.unboundid.scim2.common.BaseScimResource}.
 *
 * @see com.unboundid.scim2.common.GenericScimResource
 * @see com.unboundid.scim2.common.BaseScimResource
 */

package com.unboundid.scim2.client;
