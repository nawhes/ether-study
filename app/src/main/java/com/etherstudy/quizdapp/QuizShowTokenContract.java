package com.etherstudy.quizdapp;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.3.0.
 */
public class QuizShowTokenContract extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b5060008054600160a060020a031916331790556040805180820190915260028082527f515400000000000000000000000000000000000000000000000000000000000060209092019182526200006891816200012e565b5060408051808201909152600d8082527f5175697a53686f77546f6b656e000000000000000000000000000000000000006020909201918252620000af916003916200012e565b5060048054601260ff19909116179081905560ff16600a0a620f424002600581905560008054600160a060020a0390811682526006602090815260408084208590558354815195865290519216937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a3620001d3565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200017157805160ff1916838001178555620001a1565b82800160010185558215620001a1579182015b82811115620001a157825182559160200191906001019062000184565b50620001af929150620001b3565b5090565b620001d091905b80821115620001af5760008155600101620001ba565b90565b61176180620001e36000396000f3006080604052600436106101065763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde03811461010b578063095ea7b31461019557806318160ddd146101cd57806323b872dd146101f45780632a5a53c91461021e578063313ce5671461030a5780633ccc98ed1461033557806370a08231146103ce57806379ba5097146103ef5780637e9d44cb146104045780638da5cb5b1461057557806395d89b41146105a6578063a9059cbb146105bb578063cae9ca51146105df578063d4ee1d9014610648578063dc39d06d1461065d578063dd62ed3e14610681578063e37adc78146106a8578063f2fde38b146106bd575b600080fd5b34801561011757600080fd5b506101206106de565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561015a578181015183820152602001610142565b50505050905090810190601f1680156101875780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101a157600080fd5b506101b9600160a060020a036004351660243561076c565b604080519115158252519081900360200190f35b3480156101d957600080fd5b506101e26107d3565b60408051918252519081900360200190f35b34801561020057600080fd5b506101b9600160a060020a0360043581169060243516604435610817565b34801561022a57600080fd5b5060408051602060046024803582810135601f810185900485028601850190965285855261030895833595369560449491939091019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a99988101979196509182019450925082915084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a999881019791965091820194509250829150840183828082843750949750508435955050506020830135926040013560ff1691506109229050565b005b34801561031657600080fd5b5061031f610e13565b6040805160ff9092168252519081900360200190f35b34801561034157600080fd5b506040805160206004803580820135601f810184900484028501840190955284845261030894369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497505093359450610e1c9350505050565b3480156103da57600080fd5b506101e2600160a060020a0360043516610f32565b3480156103fb57600080fd5b50610308610f4d565b34801561041057600080fd5b5061041c600435610fd5565b6040518080602001806020018060200187815260200186815260200185815260200184810384528a818151815260200191508051906020019080838360005b8381101561047357818101518382015260200161045b565b50505050905090810190601f1680156104a05780820380516001836020036101000a031916815260200191505b5084810383528951815289516020918201918b019080838360005b838110156104d35781810151838201526020016104bb565b50505050905090810190601f1680156105005780820380516001836020036101000a031916815260200191505b5084810382528851815288516020918201918a019080838360005b8381101561053357818101518382015260200161051b565b50505050905090810190601f1680156105605780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390f35b34801561058157600080fd5b5061058a611285565b60408051600160a060020a039092168252519081900360200190f35b3480156105b257600080fd5b50610120611294565b3480156105c757600080fd5b506101b9600160a060020a03600435166024356112ec565b3480156105eb57600080fd5b50604080516020600460443581810135601f81018490048402850184019095528484526101b9948235600160a060020a031694602480359536959460649492019190819084018382808284375094975061139c9650505050505050565b34801561065457600080fd5b5061058a6114fd565b34801561066957600080fd5b506101b9600160a060020a036004351660243561150c565b34801561068d57600080fd5b506101e2600160a060020a03600435811690602435166115c7565b3480156106b457600080fd5b506101e26115f2565b3480156106c957600080fd5b50610308600160a060020a03600435166115f8565b6003805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156107645780601f1061073957610100808354040283529160200191610764565b820191906000526020600020905b81548152906001019060200180831161074757829003601f168201915b505050505081565b336000818152600760209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a35060015b92915050565b600080805260066020527f54cdd369e4e8a8515e52ca72ec816c2101831ad1f18bf44102ed171459c9b4f8546005546108119163ffffffff61163e16565b90505b90565b600160a060020a038316600090815260066020526040812054610840908363ffffffff61163e16565b600160a060020a038516600090815260066020908152604080832093909355600781528282203383529052205461087d908363ffffffff61163e16565b600160a060020a0380861660009081526007602090815260408083203384528252808320949094559186168152600690915220546108c1908363ffffffff61165316565b600160a060020a0380851660008181526006602090815260409182902094909455805186815290519193928816927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35060019392505050565b600054600160a060020a0316331461093957600080fd5b600854871061094757600080fd5b604080516000815260208101918290528051909190819081908082805b602083106109835780518252601f199092019160209182019101610964565b51815160209384036101000a60001901801990921691161790526040519190930181900381208b519095508b9450908301928392508401908083835b602083106109de5780518252601f1990920191602091820191016109bf565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b60208310610a415780518252601f199092019160209182019101610a22565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060001916141515610aaf5785600888815481101515610a8957fe5b90600052602060002090600602016000019080519060200190610aad929190611663565b505b604080516000815260208101918290528051909190819081908082805b60208310610aeb5780518252601f199092019160209182019101610acc565b51815160209384036101000a60001901801990921691161790526040519190930181900381208a519095508a9450908301928392508401908083835b60208310610b465780518252601f199092019160209182019101610b27565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b60208310610ba95780518252601f199092019160209182019101610b8a565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060001916141515610c175784600888815481101515610bf157fe5b90600052602060002090600602016001019080519060200190610c15929190611663565b505b604080516000815260208101918290528051909190819081908082805b60208310610c535780518252601f199092019160209182019101610c34565b51815160209384036101000a60001901801990921691161790526040519190930181900381208951909550899450908301928392508401908083835b60208310610cae5780518252601f199092019160209182019101610c8f565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040526040518082805190602001908083835b60208310610d115780518252601f199092019160209182019101610cf2565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051809103902060001916141515610d7f5783600888815481101515610d5957fe5b90600052602060002090600602016002019080519060200190610d7d929190611663565b505b82600888815481101515610d8f57fe5b90600052602060002090600602016003018190555081600888815481101515610db457fe5b90600052602060002090600602016004018190555080600888815481101515610dd957fe5b60009182526020909120600690910201600501805460ff191660ff928316179055811660031415610e0a5760098790555b50505050505050565b60045460ff1681565b610e246116e1565b600054600160a060020a03163314610e3b57600080fd5b506040805160c081018252848152602080820185905282518082018452600080825293830152606082018490526080820183905260a082018390526008805460018101808355919094528251805193949193859360069093027ff3f7a9fe364faab93b216da50a3214154f22a0a2b415b23a84c8169e8b636ee30192610ec5928492910190611663565b506020828101518051610ede9260018501920190611663565b5060408201518051610efa916002840191602090910190611663565b50606082015160038201556080820151600482015560a0909101516005909101805460ff191660ff9092169190911790555050505050565b600160a060020a031660009081526006602052604090205490565b600154600160a060020a03163314610f6457600080fd5b60015460008054604051600160a060020a0393841693909116917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e091a3600180546000805473ffffffffffffffffffffffffffffffffffffffff19908116600160a060020a03841617909155169055565b6060806060600080600086600880549050111515610ff257600080fd5b600880548890811061100057fe5b6000918252602091829020600690910201805460408051601f60026000196101006001871615020190941693909304928301859004850281018501909152818152928301828280156110935780601f1061106857610100808354040283529160200191611093565b820191906000526020600020905b81548152906001019060200180831161107657829003601f168201915b505050505095506008878154811015156110a957fe5b90600052602060002090600602016001018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561114e5780601f106111235761010080835404028352916020019161114e565b820191906000526020600020905b81548152906001019060200180831161113157829003601f168201915b5050505050945060088781548110151561116457fe5b600091825260209182902060026006909202018101805460408051601f6000196101006001861615020190931694909404918201859004850284018501905280835291929091908301828280156111fc5780601f106111d1576101008083540402835291602001916111fc565b820191906000526020600020905b8154815290600101906020018083116111df57829003601f168201915b5050505050935060088781548110151561121257fe5b906000526020600020906006020160030154925060088781548110151561123557fe5b906000526020600020906006020160040154915060088781548110151561125857fe5b906000526020600020906006020160050160009054906101000a900460ff1660ff16905091939550919395565b600054600160a060020a031681565b6002805460408051602060018416156101000260001901909316849004601f810184900484028201840190925281815292918301828280156107645780601f1061073957610100808354040283529160200191610764565b3360009081526006602052604081205461130c908363ffffffff61163e16565b3360009081526006602052604080822092909255600160a060020a0385168152205461133e908363ffffffff61165316565b600160a060020a0384166000818152600660209081526040918290209390935580518581529051919233927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a350600192915050565b336000818152600760209081526040808320600160a060020a038816808552908352818420879055815187815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a36040517f8f4ffcb10000000000000000000000000000000000000000000000000000000081523360048201818152602483018690523060448401819052608060648501908152865160848601528651600160a060020a038a1695638f4ffcb195948a94938a939192909160a490910190602085019080838360005b8381101561148c578181015183820152602001611474565b50505050905090810190601f1680156114b95780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b1580156114db57600080fd5b505af11580156114ef573d6000803e3d6000fd5b506001979650505050505050565b600154600160a060020a031681565b60008054600160a060020a0316331461152457600080fd5b60008054604080517fa9059cbb000000000000000000000000000000000000000000000000000000008152600160a060020a0392831660048201526024810186905290519186169263a9059cbb926044808401936020939083900390910190829087803b15801561159457600080fd5b505af11580156115a8573d6000803e3d6000fd5b505050506040513d60208110156115be57600080fd5b50519392505050565b600160a060020a03918216600090815260076020908152604080832093909416825291909152205490565b60095481565b600054600160a060020a0316331461160f57600080fd5b6001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b60008282111561164d57600080fd5b50900390565b818101828110156107cd57600080fd5b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106116a457805160ff19168380011785556116d1565b828001600101855582156116d1579182015b828111156116d15782518255916020019190600101906116b6565b506116dd92915061171b565b5090565b60c0604051908101604052806060815260200160608152602001606081526020016000815260200160008152602001600060ff1681525090565b61081491905b808211156116dd57600081556001016117215600a165627a7a72305820dbb7f12c54e9c09e06ebf60afb67035a2bf6490c44957745dd56ac3e893c13d20029";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_EDITQUIZLIST = "editQuizList";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_RESERVEQUIZ = "reserveQuiz";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_GETQUIZLIST = "getQuizList";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_NEWOWNER = "newOwner";

    public static final String FUNC_TRANSFERANYERC20TOKEN = "transferAnyERC20Token";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_LASTNO = "LastNo";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected QuizShowTokenContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected QuizShowTokenContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected QuizShowTokenContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected QuizShowTokenContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String spender, BigInteger tokens) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new Address(spender),
                new Uint256(tokens)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String from, String to, BigInteger tokens) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new Address(from),
                new Address(to),
                new Uint256(tokens)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> editQuizList(BigInteger _no, String _datetime, String _prize_kind, String _hash_db, BigInteger _prize_amount, BigInteger _no_winner, BigInteger _state) {
        final Function function = new Function(
                FUNC_EDITQUIZLIST, 
                Arrays.<Type>asList(new Uint256(_no),
                new Utf8String(_datetime),
                new Utf8String(_prize_kind),
                new Utf8String(_hash_db),
                new Uint256(_prize_amount),
                new Uint256(_no_winner),
                new Uint8(_state)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> reserveQuiz(String _datetime, String _prize_kind, BigInteger _prize_amount) {
        final Function function = new Function(
                FUNC_RESERVEQUIZ, 
                Arrays.<Type>asList(new Utf8String(_datetime),
                new Utf8String(_prize_kind),
                new Uint256(_prize_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String tokenOwner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new Address(tokenOwner)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        final Function function = new Function(
                FUNC_ACCEPTOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple6<String, String, String, BigInteger, BigInteger, BigInteger>> getQuizList(BigInteger _no) {
        final Function function = new Function(FUNC_GETQUIZLIST, 
                Arrays.<Type>asList(new Uint256(_no)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple6<String, String, String, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple6<String, String, String, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<String, String, String, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<String, String, String, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String to, BigInteger tokens) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new Address(to),
                new Uint256(tokens)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String spender, BigInteger tokens, byte[] data) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL, 
                Arrays.<Type>asList(new Address(spender),
                new Uint256(tokens),
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> newOwner() {
        final Function function = new Function(FUNC_NEWOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferAnyERC20Token(String tokenAddress, BigInteger tokens) {
        final Function function = new Function(
                FUNC_TRANSFERANYERC20TOKEN, 
                Arrays.<Type>asList(new Address(tokenAddress),
                new Uint256(tokens)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String tokenOwner, String spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new Address(tokenOwner),
                new Address(spender)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> LastNo() {
        final Function function = new Function(FUNC_LASTNO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new Address(_newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.tokenOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    @Deprecated
    public static QuizShowTokenContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new QuizShowTokenContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static QuizShowTokenContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new QuizShowTokenContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static QuizShowTokenContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new QuizShowTokenContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static QuizShowTokenContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new QuizShowTokenContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<QuizShowTokenContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(QuizShowTokenContract.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<QuizShowTokenContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(QuizShowTokenContract.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<QuizShowTokenContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(QuizShowTokenContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<QuizShowTokenContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(QuizShowTokenContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String _from;

        public String _to;
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger tokens;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String tokenOwner;

        public String spender;

        public BigInteger tokens;
    }
}
