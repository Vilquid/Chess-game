package cin3.chess;

import cin3.chess.services.ChessGameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

//@ExtendWith(PowerMockRunner.class)
//@PrepareForTest(ChessGameService.class)
//@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
//public class TimeElapsedTest
//{
//	@Test
//	public void getTimeElapsedTest()
//	{
//		ChessGameService service = new ChessGameService();
//
//		PowerMockito.mockStatic(System.class);
//		when(System.currentTimeMillis()).thenReturn(1000000L);
//
//		assertThat(service.getTimeElapsed(900000L)).isEqualTo(100);
//		assertThat(service.getTimeElapsed(null)).isEqualTo(0);
//	}
//}
