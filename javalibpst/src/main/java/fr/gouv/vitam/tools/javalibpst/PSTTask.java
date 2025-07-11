/**
 * Copyright 2010 Richard Johnson & Orin Eman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---
 *
 * This file is part of javalibpst.
 *
 * javalibpst is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * javalibpst is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with javalibpst. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.gouv.vitam.tools.javalibpst;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Object that represents Task items
 *
 * @author Richard Johnson
 */
public class PSTTask extends PSTMessage {

    /**
     * Instantiates a new Pst task.
     *
     * @param theFile             the the file
     * @param descriptorIndexNode the descriptor index node
     * @throws PSTException the pst exception
     * @throws IOException  the io exception
     */
    public PSTTask(final PSTFile theFile, final DescriptorIndexNode descriptorIndexNode)
        throws PSTException, IOException {
        super(theFile, descriptorIndexNode);
    }

    /**
     * Instantiates a new Pst task.
     *
     * @param theFile              the the file
     * @param folderIndexNode      the folder index node
     * @param table                the table
     * @param localDescriptorItems the local descriptor items
     */
    public PSTTask(final PSTFile theFile, final DescriptorIndexNode folderIndexNode, final PSTTableBC table,
        final HashMap<Integer, PSTDescriptorItem> localDescriptorItems) {
        super(theFile, folderIndexNode, table, localDescriptorItems);
    }

    /**
     * Status Integer 32-bit signed 0x0 =&gt; Not started
     *
     * @return the task status
     */
    public int getTaskStatus() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x00008101, PSTFile.PSETID_Task));
    }

    /**
     * Percent Complete Floating point double precision (64-bit)
     *
     * @return the percent complete
     */
    public double getPercentComplete() {
        return this.getDoubleItem(this.pstFile.getNameToIdMapItem(0x00008102, PSTFile.PSETID_Task));
    }

    /**
     * Is team task Boolean
     *
     * @return the boolean
     */
    public boolean isTeamTask() {
        return this.getBooleanItem(this.pstFile.getNameToIdMapItem(0x00008103, PSTFile.PSETID_Task));
    }

    /**
     * Date completed Filetime
     *
     * @return the task date completed
     */
    public Date getTaskDateCompleted() {
        return this.getDateItem(this.pstFile.getNameToIdMapItem(0x0000810f, PSTFile.PSETID_Task));
    }

    /**
     * Actual effort in minutes Integer 32-bit signed
     *
     * @return the task actual effort
     */
    public int getTaskActualEffort() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x00008110, PSTFile.PSETID_Task));
    }

    /**
     * Total effort in minutes Integer 32-bit signed
     *
     * @return the task estimated effort
     */
    public int getTaskEstimatedEffort() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x00008111, PSTFile.PSETID_Task));
    }

    /**
     * Task version Integer 32-bit signed FTK: Access count
     *
     * @return the task version
     */
    public int getTaskVersion() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x00008112, PSTFile.PSETID_Task));
    }

    /**
     * Complete Boolean
     *
     * @return the boolean
     */
    public boolean isTaskComplete() {
        return this.getBooleanItem(this.pstFile.getNameToIdMapItem(0x0000811c, PSTFile.PSETID_Task));
    }

    /**
     * Owner ASCII or Unicode string
     *
     * @return the task owner
     */
    public String getTaskOwner() {
        return this.getStringItem(this.pstFile.getNameToIdMapItem(0x0000811f, PSTFile.PSETID_Task));
    }

    /**
     * Delegator ASCII or Unicode string
     *
     * @return the task assigner
     */
    public String getTaskAssigner() {
        return this.getStringItem(this.pstFile.getNameToIdMapItem(0x00008121, PSTFile.PSETID_Task));
    }

    /**
     * Unknown ASCII or Unicode string
     *
     * @return the task last user
     */
    public String getTaskLastUser() {
        return this.getStringItem(this.pstFile.getNameToIdMapItem(0x00008122, PSTFile.PSETID_Task));
    }

    /**
     * Ordinal Integer 32-bit signed
     *
     * @return the task ordinal
     */
    public int getTaskOrdinal() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x00008123, PSTFile.PSETID_Task));
    }

    /**
     * Is recurring Boolean
     *
     * @return the boolean
     */
    public boolean isTaskFRecurring() {
        return this.getBooleanItem(this.pstFile.getNameToIdMapItem(0x00008126, PSTFile.PSETID_Task));
    }

    /**
     * Role ASCII or Unicode string
     *
     * @return the task role
     */
    public String getTaskRole() {
        return this.getStringItem(this.pstFile.getNameToIdMapItem(0x00008127, PSTFile.PSETID_Task));
    }

    /**
     * Ownership Integer 32-bit signed
     *
     * @return the task ownership
     */
    public int getTaskOwnership() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x00008129, PSTFile.PSETID_Task));
    }

    /**
     * Delegation State
     *
     * @return the acceptance state
     */
    public int getAcceptanceState() {
        return this.getIntItem(this.pstFile.getNameToIdMapItem(0x0000812a, PSTFile.PSETID_Task));
    }

    @Override
    public String toString() {
        return "Status Integer 32-bit signed 0x0 => Not started [TODO]: " + this.getTaskStatus() + "\n"
            + "Percent Complete Floating point double precision (64-bit): " + this.getPercentComplete() + "\n"
            + "Is team task Boolean: " + this.isTeamTask() + "\n" + "Start date Filetime: " + this.getTaskStartDate()
            + "\n" + "Due date Filetime: " + this.getTaskDueDate() + "\n" + "Date completed Filetime: "
            + this.getTaskDateCompleted() + "\n" + "Actual effort in minutes Integer 32-bit signed: "
            + this.getTaskActualEffort() + "\n" + "Total effort in minutes Integer 32-bit signed: "
            + this.getTaskEstimatedEffort() + "\n" + "Task version Integer 32-bit signed FTK: Access count: "
            + this.getTaskVersion() + "\n" + "Complete Boolean: " + this.isTaskComplete() + "\n"
            + "Owner ASCII or Unicode string: " + this.getTaskOwner() + "\n" + "Delegator ASCII or Unicode string: "
            + this.getTaskAssigner() + "\n" + "Unknown ASCII or Unicode string: " + this.getTaskLastUser() + "\n"
            + "Ordinal Integer 32-bit signed: " + this.getTaskOrdinal() + "\n" + "Is recurring Boolean: "
            + this.isTaskFRecurring() + "\n" + "Role ASCII or Unicode string: " + this.getTaskRole() + "\n"
            + "Ownership Integer 32-bit signed: " + this.getTaskOwnership() + "\n" + "Delegation State: "
            + this.getAcceptanceState();

    }
}
