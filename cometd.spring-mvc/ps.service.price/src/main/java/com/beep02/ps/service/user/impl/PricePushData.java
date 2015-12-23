package com.beep02.ps.service.user.impl;

import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON.Output;

import com.beep02.ps.datastream.api.PushData;




/**

Price data per pair

@author Vladimir Nabokov
@since 11/06/2013
 **/

public class PricePushData extends PushData{
	
	String bid;
	String offer;
	String pair;
	String par1;
	String par2;
	String par3;
	String par4;
	String par5;
	String par6;
	String par7;
	String par8;
	String par9;
	String par10;
	String par11;
	String par12;
	String par13;
	String par14;
	String par15;
	String par16;
	String par17;
	String par18;
	String par19;
	String par20;
	String par21;
	String par22;
	String par23;
	String par24;
	String par25;
	String par26;
	String par27;
	String par28;
	String par29;
	String par30;
	String par31;
	String par32;
	String par33;
	String par34;
	String par35;
	
	
	
	
	public PricePushData(){}
	
	public PricePushData(String bid, String offer, String pair, String par1,String  par2,String  par3,String  par4,String  par5,String  par6,String  par7,String  par8, String par9,String  par10, String par11,
			String par12,String par13,String par14,String par15,String par16,String par17,String par18,String par19,String par20,String par21,String par22,String par23,String par24,String par25, String par26, String par27, String par28, String par29, String par30, String par31, String par32, String par33, String par34, String par35) {
		super();
		this.bid = bid;
		this.offer = offer;
		this.pair = pair;
		this.par1 = par1;
		this.par2 = par2;
		this.par3 = par3;
		this.par4 = par4;
		this.par5 = par5;
		this.par6 = par6;
		this.par7 = par7;
		this.par8 = par8;
		this.par9 = par9;
		this.par10 = par10;
		this.par11 = par11;
		this.par12 = par12;
		this.par13 = par13;
		this.par14 = par14;
		this.par15 = par15;
		this.par16 = par16;
		this.par17 = par17;
		this.par18 = par18;
		this.par19 = par19;
		this.par20 = par20;
		this.par21 = par21;
		this.par22 = par22;
		this.par23 = par23;
		this.par24 = par24;
		this.par25 = par25;
		this.par26 = par26;
		this.par27 = par27;
		this.par28 = par28;
		this.par29 = par29;
		this.par30 = par30;
		this.par31 = par31;
		this.par32 = par32;
		this.par33 = par33;
		this.par34 = par34;
		this.par35 = par35;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getOffer() {
		return offer;
	}
	public void setOffer(String offer) {
		this.offer = offer;
	}
	public String getPair() {
		return pair;
	}
	public void setPair(String pair) {
		this.pair = pair;
	}

	@Override
	public void toJSON(Output out) {
		super.toJSON(out);
		out.add("bid",bid);
        out.add("offer",offer);
        out.add("pair",pair);
        out.add("par1",par1);
        out.add("par2",par2);
        out.add("par3",par3);
        out.add("par4",par4);
        out.add("par5",par5);
        out.add("par6",par6);
        out.add("par7",par7);
        out.add("par8",par8);
        out.add("par9",par9);
        out.add("par10",par10);
        out.add("par11",par11);
        out.add("par12",par12);
        out.add("par13",par13);
        out.add("par14",par14);
        out.add("par15",par15);
        out.add("par16",par16);
        out.add("par17",par17);
        out.add("par18",par18);
        out.add("par19",par19);
        out.add("par20",par20);
        out.add("par21",par21);
        out.add("par22",par22);
        out.add("par23",par23);
        out.add("par24",par24);
        out.add("par25",par25);
        out.add("par26",par26);
        out.add("par27",par27);
        out.add("par28",par28);
        out.add("par29",par29);
        out.add("par30",par30);
        out.add("par31",par31);
        out.add("par32",par32);
        out.add("par33",par33);
        out.add("par34",par34);
        out.add("par35",par35);
    }

	@Override
	@SuppressWarnings("all")
	public void fromJSON(Map object) {
	}
	
	public PricePushData clone()
    {
        try
        {
            return (PricePushData)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

	public String getPar1() {
		return par1;
	}

	public void setPar1(String par1) {
		this.par1 = par1;
	}

	public String getPar2() {
		return par2;
	}

	public void setPar2(String par2) {
		this.par2 = par2;
	}

	public String getPar3() {
		return par3;
	}

	public void setPar3(String par3) {
		this.par3 = par3;
	}

	public String getPar4() {
		return par4;
	}

	public void setPar4(String par4) {
		this.par4 = par4;
	}

	public String getPar5() {
		return par5;
	}

	public void setPar5(String par5) {
		this.par5 = par5;
	}

	public String getPar6() {
		return par6;
	}

	public void setPar6(String par6) {
		this.par6 = par6;
	}

	public String getPar7() {
		return par7;
	}

	public void setPar7(String par7) {
		this.par7 = par7;
	}

	public String getPar8() {
		return par8;
	}

	public void setPar8(String par8) {
		this.par8 = par8;
	}

	public String getPar9() {
		return par9;
	}

	public void setPar9(String par9) {
		this.par9 = par9;
	}

	public String getPar10() {
		return par10;
	}

	public void setPar10(String par10) {
		this.par10 = par10;
	}

	public String getPar11() {
		return par11;
	}

	public void setPar11(String par11) {
		this.par11 = par11;
	}

	public String getPar12() {
		return par12;
	}

	public void setPar12(String par12) {
		this.par12 = par12;
	}

	public String getPar13() {
		return par13;
	}

	public void setPar13(String par13) {
		this.par13 = par13;
	}

	public String getPar14() {
		return par14;
	}

	public void setPar14(String par14) {
		this.par14 = par14;
	}

	public String getPar15() {
		return par15;
	}

	public void setPar15(String par15) {
		this.par15 = par15;
	}

	public String getPar16() {
		return par16;
	}

	public void setPar16(String par16) {
		this.par16 = par16;
	}

	public String getPar17() {
		return par17;
	}

	public void setPar17(String par17) {
		this.par17 = par17;
	}

	public String getPar18() {
		return par18;
	}

	public void setPar18(String par18) {
		this.par18 = par18;
	}

	public String getPar19() {
		return par19;
	}

	public void setPar19(String par19) {
		this.par19 = par19;
	}

	public String getPar20() {
		return par20;
	}

	public void setPar20(String par20) {
		this.par20 = par20;
	}

	public String getPar21() {
		return par21;
	}

	public void setPar21(String par21) {
		this.par21 = par21;
	}

	public String getPar22() {
		return par22;
	}

	public void setPar22(String par22) {
		this.par22 = par22;
	}

	public String getPar23() {
		return par23;
	}

	public void setPar23(String par23) {
		this.par23 = par23;
	}

	public String getPar24() {
		return par24;
	}

	public void setPar24(String par24) {
		this.par24 = par24;
	}

	public String getPar25() {
		return par25;
	}

	public void setPar25(String par25) {
		this.par25 = par25;
	}

	public String getPar26() {
		return par26;
	}

	public void setPar26(String par26) {
		this.par26 = par26;
	}

	public String getPar27() {
		return par27;
	}

	public void setPar27(String par27) {
		this.par27 = par27;
	}

	public String getPar28() {
		return par28;
	}

	public void setPar28(String par28) {
		this.par28 = par28;
	}

	public String getPar29() {
		return par29;
	}

	public void setPar29(String par29) {
		this.par29 = par29;
	}

	public String getPar30() {
		return par30;
	}

	public void setPar30(String par30) {
		this.par30 = par30;
	}

	public String getPar31() {
		return par31;
	}

	public void setPar31(String par31) {
		this.par31 = par31;
	}

	public String getPar32() {
		return par32;
	}

	public void setPar32(String par32) {
		this.par32 = par32;
	}

	public String getPar33() {
		return par33;
	}

	public void setPar33(String par33) {
		this.par33 = par33;
	}

	public String getPar34() {
		return par34;
	}

	public void setPar34(String par34) {
		this.par34 = par34;
	}

	public String getPar35() {
		return par35;
	}

	public void setPar35(String par35) {
		this.par35 = par35;
	}
	

}
